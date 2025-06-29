// =====================
// Глобальные переменные
// =====================
const state = {
    stompClient: null,
    currentSubscription: null,
    currentChat: null,
    menuToggle: false
};

// DOM элементы
const elements = {
    titleChat: document.querySelector('.titleChat'),
    chatInput: document.querySelector('.chatInput'),
    chatList: document.querySelector('.chatList'),
    chatContainer: document.querySelector('.chat'),
    chat: document.querySelector('.chat'),
    main: document.querySelector('main'),
    aside: document.querySelector('aside'),
    menu: document.querySelector('.menu'),
    asideHeader: document.querySelector('.aside-header'),
    createGroupChatForm: document.getElementById('createGroupChatForm'),
    addChatList: document.querySelector('.addChatList'),
    groupNameInput: document.getElementById('groupNameInput')
};

// =====================
// Инициализация приложения
// =====================
async function initApp() {
    try {
        await validateToken();
        await fetchUserChats();
        setupEventListeners();
        updateLayout();
        updateAppHeight();
    } catch (error) {
        console.error('Initialization error:', error);
        alert('Ошибка авторизации');
        // window.location.href = '/authorization.html';
    }
}

// =====================
// Функции работы с API
// =====================
async function validateToken() {
    const token = localStorage.getItem('token');
    const response = await fetch('/validateToken', {
        method: 'POST',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify({value: token})
    });
    if (!response.ok) throw new Error('Invalid token');
}

async function fetchUserChats() {
    const token = localStorage.getItem('token');
    const response = await fetch('/getAllUserChats', {
        method: 'POST',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify({value: token})
    });

    if (!response.ok) throw new Error('Failed to load chats');

    const chats = await response.json();
    renderChatList(chats);
}

// =====================
// Функции рендеринга
// =====================
function renderChatList(chats) {
    elements.chatList.innerHTML = '';
    elements.addChatList.innerHTML = '';

    chats.forEach(chat => {
        const chatElement = createChatElement(chat);
        elements.chatList.appendChild(chatElement);

        if (chat.isGroupChat) {
            const groupChatElement = createChatElement(chat, false);
            elements.addChatList.appendChild(groupChatElement);
        }
    });
}

function createChatElement(chat, isCheckbox) {
    const chatElement = document.createElement('div');
    chatElement.classList.add('chatInf');
    chatElement.textContent = chat.name;
    chatElement.dataset.chatId = chat.id;

    if (isCheckbox) {
        const checkbox = document.createElement('input');
        checkbox.type = 'checkbox';
        chatElement.appendChild(checkbox);
    }

    return chatElement;
}

function addMessageToChat(content, sender) {
    // ... (логика рендеринга сообщений)
}

// =====================
// Функции работы с WebSocket
// =====================
async function connect(){
    if (!state.stompClient) {
        const socket = new SockJS('/ws/chat');
        state.stompClient = Stomp.over(socket);

        state.stompClient.debug = (str) => console.log('STOMP DEBUG:', str);

        return new Promise((resolve, reject) => {
            state.stompClient.connect({}, () => resolve(), (error) => reject(error));
        });
    }
    return Promise.resolve();
}

async function subscribeToChat(chatId) {
    if (state.currentSubscription) {
        await state.currentSubscription.unsubscribe();
    }

    state.currentSubscription = state.stompClient.subscribe(
        `/topic/chat${chatId}`,
        message => {
            const msg = JSON.parse(message.body);
            addMessageToChat(msg.content, msg.sender);
        }
    );
}

// =====================
// Обработчики событий
// =====================
function setupEventListeners() {
    // Навигация по чатам
    elements.chatList.addEventListener('click', (event) => {
        if (event.target.classList.contains('chatInf')) {
            selectChat(event.target);
        }
    });

    // Отправка сообщений
    document.getElementById('sendMessageForm').addEventListener('submit', (event) => {
        event.preventDefault();
        if (!elements.currentChat || !elements.currentChat.dataset.chatId) {
            alert("Сначала выберите чат!");
            return;
        }

        const message = elements.chatInput.value;
        if (!message) return;

        state.stompClient.send("/app/send", {}, JSON.stringify({
            content: message,
            chatId: parseInt(elements.currentChat.dataset.chatId),
            sender: localStorage.getItem('username')
        }));

        elements.chatInput.value = '';
    });

    // Поиск чатов
    document.getElementById('asideSearchForm').addEventListener('submit', async (e) => {
        e.preventDefault();
        const searchValue = document.getElementById('asideSearch').value.trim();
        if (!searchValue) return;

        // Поиск существующего чата
        const chatInfs = document.querySelectorAll('.chatInf');
        for (const chat of chatInfs) {
            if (chat.textContent === searchValue) {
                selectChat(chat);
                return;
            }
        }

        // Создание нового чата
        try {
            const userRes = await fetch('/searchUser', {
                method: 'POST',
                headers: {'Content-Type': 'application/json'},
                body: JSON.stringify({username: searchValue})
            });

            if (await userRes.text() === 'Success') {
                const names = [localStorage.getItem('token'), searchValue];
                const chatRes = await fetch('/grChat', {
                    method: 'POST',
                    headers: {'Content-Type': 'application/json'},
                    body: JSON.stringify(names)
                });

                const chatId = await chatRes.json();
                const chatInf = document.createElement('div');
                chatInf.classList.add('chatInf');
                chatInf.textContent = searchValue;
                chatInf.dataset.chatId = chatId;
                document.querySelector('.chatList').appendChild(chatInf);
                selectChat(chatInf);
            }
        } catch (err) {
            console.error('Search error:', err);
            alert('Ошибка поиска');
        }
    });



    // Создание группового чата
    elements.createGroupChatForm.addEventListener('submit', async (event) => {
        event.preventDefault();

        const items = elements.addChatList.querySelectorAll('.chatInf');
        let participants = [];

        items.forEach(item => {
            const flag = item.querySelector('input[type="checkbox"]');

            if (flag) {
                participants.push(item.textContent);
            }
        })

        await fetch('/createGroup', {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify({name: elements.groupNameInput.value, participants:participants, creatorName:localStorage.getItem('token')})
        }).then(async response => {
            return await response.json();
        }).then(chatId => {
            const chatInf = document.createElement('div');
            chatInf.classList.add('chatInf');
            chatInf.textContent = elements.groupNameInput.value;
            chatInf.dataset.chatId = chatId;
            document.querySelector('.chatList').appendChild(chatInf);
            selectChat(chatInf);
        })

        elements.asideHeader.style.display = 'grid';
        elements.createGroupChatForm.style.display = 'none';
        elements.chatList.style.display = 'flex';
    })



    // Обработчики изменения размера окна
    window.addEventListener('resize', () => {
        updateAppHeight();
        updateLayout();
    });
    window.addEventListener('orientationchange', updateAppHeight);
    window.addEventListener('beforeunload', () => {
        if (state.stompClient?.connected) state.stompClient.disconnect();
    });
}

// =====================
// Вспомогательные функции
// =====================
function updateAppHeight() {
    document.documentElement.style.setProperty('--app-height', `${window.innerHeight}px`);
}

function updateLayout() {
    const isMobile = window.innerWidth <= 800;
    elements.aside.style.display = isMobile && state.currentChat ? 'none' : 'block';
    elements.main.style.display = isMobile && !state.currentChat ? 'none' : 'grid';
}

async function selectChat(chatElement) {
    if (!chatElement) return;

    elements.titleChat.textContent = chatElement.textContent;
    elements.currentChat = chatElement;
    elements.chat.innerHTML = '';

    await getAllMess(chatElement.dataset.chatId, 1, 30);

    try {
        await connect();

        if (currentSubscription) {
            currentSubscription.unsubscribe();
        }

        currentSubscription = state.stompClient.subscribe(
            `/topic/chat${chatElement.dataset.chatId}`,
            (message) => {
                const msg = JSON.parse(message.body);
                addMessageToChat(msg.content, msg.sender);
            }
        );

        console.log(`Подключено к чату ${chatElement.dataset.chatId}`);
    } catch (error) {
        console.error('Ошибка подключения:', error);
    }

    updateLayout();
}

async function getAllMess(chatId, page, size) {
    await fetch(`/api/chats/${chatId}/messages?page=${page}&size=${size}`)
        .then(res => res.json()).then(data => {
            const messages = data.content
            messages.forEach(message => {
            addMessageToChat(message.content, message.sender);
        })
    });
}



// =====================
// Инициализация при загрузке
// =====================
document.addEventListener('DOMContentLoaded', initApp);
// =====================
// Глобальные переменные
// =====================
const state = {
    stompClient: null,
    currentSubscription: null,
    currentChat: null,
    menuToggle: false,
    isLoading: false,
    hasMoreMessages: true,
    allMessages: [],
    pageSize: 20,
    currentPage: 0
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
        await connect();
        await validateToken();
        await fetchUserChats();
        setupEventListeners();
        setupScrollListener();
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

function addMessageToChat(content, sender, isBefore = true) {
    const msg = document.createElement('div');
    const msgElement = document.createElement('div');
    msgElement.style.display = 'inline-block';
    msgElement.style.borderRadius = '20px';
    msgElement.style.backgroundColor = '#212121';
    msgElement.style.padding = '15px';
    msgElement.textContent = content;
    if (sender === localStorage.getItem('username')) {
        msgElement.style.backgroundColor = 'mediumslateblue';
        msg.style.textAlign = 'right';
    }
    msg.style.margin = '10px';
    msg.appendChild(msgElement);

    if (isBefore) {
        elements.chat.insertBefore(msg, elements.chat.firstChild);
    } else {
        elements.chat.appendChild(msg)
    }

    elements.chat.scrollTop = elements.chat.scrollHeight;
}

async function getAllMess(chatId, isInitialLoad = false) {
    if (state.isLoading || !state.hasMoreMessages) return;

    state.isLoading = true;

    try {
        const response = await fetch(`/api/chats/getMessages?page=${state.currentPage}&size=${state.pageSize}`, {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify({
                token: localStorage.getItem('token'),
                chatId: chatId
            }),
        });

        const data = await response.json();
        const messages = data.content;

        if (messages.length === 0) {
            state.hasMoreMessages = false;
        } else {
            state.allMessages = messages;
            messages.forEach((message) => {
                addMessageToChat(message.content, message.sender);
            })

            state.currentPage++;
        }
    } catch (error) {
        console.error('Ошибка загрузки сообщений:', error);
    } finally {
        state.isLoading = false;

        if (isInitialLoad) {
            elements.chat.scrollTop = elements.chat.scrollHeight;
        }
    }
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
    try {
        await connect();

        if (state.currentSubscription) {
            state.currentSubscription.unsubscribe();
        }

        state.currentSubscription = state.stompClient.subscribe(
            `/topic/chats/${chatId}/messages`,
            (message) => {
                const msg = JSON.parse(message.body);
                addMessageToChat(msg.content, msg.sender, false);
            }
        );

        console.log(`Подключено к чату ${chatId}`);
    } catch (error) {
        console.error('Ошибка подключения:', error);
    }
}

// =====================
// Обработчики событий
// =====================
function setupEventListeners() {
    // Навигация по чатам
    elements.chatList.addEventListener('click', async (event) => {
        if (event.target.classList.contains('chatInf')) {
            await selectChat(event.target);
        }
    });

    // Отправка сообщений
    document.getElementById('sendMessageForm').addEventListener('submit', (event) => {
        event.preventDefault();
        if (!state.currentChat || !state.currentChat.dataset.chatId) {
            alert("Сначала выберите чат!");
            return;
        }

        const message = elements.chatInput.value;
        if (!message) return;

        state.stompClient.send(`/app/chats/${state.currentChat.dataset.chatId}/messages`, {
            token: localStorage.getItem('token')
        }, JSON.stringify({
            content: message
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
                await selectChat(chat);
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
                await selectChat(chatInf);
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

    state.currentPage = 0;
    state.isLoading = false;
    state.hasMoreMessages = true;
    state.allMessages = [];


    await subscribeToChat(chatElement.dataset.chatId);

    elements.titleChat.textContent = chatElement.textContent;
    state.currentChat = chatElement;
    elements.chat.innerHTML = '';

    await getAllMess(chatElement.dataset.chatId);

    updateLayout();
}

function setupScrollListener() {
    elements.chat.addEventListener('scroll', async () => {
        if (state.isLoading || !state.hasMoreMessages) return;

        if (elements.chat.scrollTop < state.pageSize) {
            await getAllMess(state.currentChat.dataset.chatId);
        }
    });
}



// =====================
// Инициализация при загрузке
// =====================
document.addEventListener('DOMContentLoaded', initApp);
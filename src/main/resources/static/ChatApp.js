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
        headers: {'Content-Type': 'text/plain'},
        body: token
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

        if (!chat.isGroupChat) {
            const groupChatElement = createChatElement(chat, true);
            elements.addChatList.appendChild(groupChatElement);
        }
    });
}

function createChatElement(chat, isCheckbox = false) {
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
async function connectWebSocket() {
    if (state.stompClient) return;

    const socket = new SockJS('/ws/chat');
    state.stompClient = Stomp.over(socket);
    state.stompClient.debug = () => {}; // Отключить логи

    return new Promise((resolve, reject) => {
        state.stompClient.connect({}, resolve, reject);
    });
}

async function subscribeToChat(chatId) {
    if (state.currentSubscription) {
        state.currentSubscription.unsubscribe();
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
    elements.chatList.addEventListener('click', handleChatSelect);

    // Отправка сообщений
    document.getElementById('sendMessageForm').addEventListener('submit', handleMessageSubmit);

    // Поиск чатов
    document.getElementById('asideSearchForm').addEventListener('submit', handleSearch);

    // Управление меню
    document.querySelector('.menuButton').addEventListener('click', toggleMenu);
    document.getElementById('createGroupButton').addEventListener('click', showGroupForm);
    document.getElementById('crGrChFormBackButton').addEventListener('click', hideGroupForm);

    // Создание группового чата
    elements.createGroupChatForm.addEventListener('submit', handleGroupCreate);

    // Кнопка "Назад" в мобильной версии
    document.querySelector('.mobileBackButton').addEventListener('click', () => {
        state.currentChat = null;
        updateLayout();
    });

    // Обработчики изменения размера окна
    window.addEventListener('resize', handleResize);
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

// =====================
// Инициализация при загрузке
// =====================
document.addEventListener('DOMContentLoaded', initApp);
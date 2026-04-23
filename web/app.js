// CONFIGURAÇÃO: COLE A URL DO SEU GOOGLE APPS SCRIPT AQUI
const API_URL = "https://script.google.com/macros/s/AKfycbxxkxzEU8hjAqDfDlbk1t5w6t_-2DqwvzEY8fmIEe9vSZfv81ctidf_pJ6EgbsH1J1Y/exec";

// Mock Data (Caso a API não esteja configurada ainda)
const MOCK_DATA = {
    banners: [
        { Titulo: "Culto de Celebração", Subtitulo: "Domingo às 19h", ImagemUrl: "https://images.unsplash.com/photo-1438232992991-995b7058bbb3?q=80&w=1000" },
        { Titulo: "Escola Bíblica", Subtitulo: "Domingo às 9h", ImagemUrl: "https://images.unsplash.com/photo-1504052434569-70ad5836ab65?q=80&w=1000" }
    ],
    noticias: [
        { Titulo: "Conferência de Jovens 2026", Data: "25/10/2026 às 19h", Descricao: "Um tempo de renovo e propósito para a juventude.", ImagemUrl: "https://images.unsplash.com/photo-1511795409834-ef04bbd61622?q=80&w=500" },
        { Titulo: "Novo Horário de Cultos", Data: "20/11/2026 às 18:30", Descricao: "Venha celebrar conosco em um culto de adoração profunda.", ImagemUrl: "https://images.unsplash.com/photo-1438232992991-995b7058bbb3?q=80&w=500" }
    ],
    sermoes: [
        { Titulo: "Vivendo pela Fé", Pregador: "Pr. João Silva", Data: "15/04/2026", ImagemUrl: "https://images.unsplash.com/photo-1507679799987-c73779587ccf?q=80&w=500", VideoUrl: "https://www.youtube.com/watch?v=dQw4w9WgXcQ" },
        { Titulo: "O Poder da Oração", Pregador: "Pra. Maria", Data: "10/04/2026", ImagemUrl: "https://images.unsplash.com/photo-1499209974431-9dac3adaf471?q=80&w=500", VideoUrl: "" }
    ],
    ministerios: [
        { Nome: "Louvor", Lider: "Davi", Descricao: "Ministério de música e adoração.", ImagemUrl: "https://images.unsplash.com/photo-1510915361894-db8b60106cb1?q=80&w=500" },
        { Nome: "Infantil", Lider: "Sara", Descricao: "Ensino bíblico para crianças.", ImagemUrl: "https://images.unsplash.com/photo-1484662020986-75935d2ebc66?q=80&w=500" }
    ],
    mensagemLider: [
        { Nome: "Pr. Presidente", Cargo: "Pastor Sênior", Mensagem: "A Importância da Fé e da Perseverança", VideoUrl: "https://www.youtube.com/watch?v=dQw4w9WgXcQ", FotoUrl: "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?q=80&w=200" },
        { Nome: "Pra. Auxiliar", Cargo: "Pastora de Famílias", Mensagem: "Família sob a Graça: O Fundamento do Lar", VideoUrl: "https://drive.google.com/file/d/1_PLACEHOLDER_ID/view?usp=sharing", FotoUrl: "https://images.unsplash.com/photo-1494790108377-be9c29b29330?q=80&w=200" }
    ],
    eventos: [
        { Titulo: "Culto da Família", Data: "Domingo", Horario: "19:00", Local: "Sede" },
        { Titulo: "Escola Bíblica", Data: "Domingo", Horario: "09:00", Local: "Sede" },
        { Titulo: "Células", Data: "Quarta-feira", Horario: "20:00", Local: "Casas" },
        { Titulo: "Culto de Jovens", Data: "Sábado", Horario: "19:30", Local: "Anexo" }
    ]
};

let currentGivingType = 'dizimo';
let bannerInterval = null;

// Converte URLs do YouTube e Google Drive para o formato de incorporação (Embed/Preview)
function getEmbedUrl(url) {
    if (!url) return '';

    // Suporte para Google Drive
    if (url.includes('drive.google.com')) {
        let fileId = '';
        if (url.includes('/d/')) {
            fileId = url.split('/d/')[1].split('/')[0].split('?')[0];
        } else if (url.includes('id=')) {
            fileId = new URLSearchParams(new URL(url).search).get('id');
        }
        return fileId ? `https://drive.google.com/file/d/${fileId}/preview` : url;
    }

    // Suporte para YouTube
    if (url.includes('youtube.com/embed/')) return url;
    let videoId = '';
    if (url.includes('youtu.be/')) {
        videoId = url.split('youtu.be/')[1].split('?')[0];
    } else if (url.includes('youtube.com/watch')) {
        videoId = new URLSearchParams(new URL(url).search).get('v');
    } else if (url.includes('youtube.com/shorts/')) {
        videoId = url.split('shorts/')[1].split('?')[0];
    }

    return videoId ? `https://www.youtube.com/embed/${videoId}` : url;
}

// Função global para formatar data e hora no padrão brasileiro
const formatDateBR = (dateValue, compact = false) => {
    if (!dateValue) return '';

    const dateStr = dateValue.toString();
    const dateObj = new Date(dateValue);

    if (!isNaN(dateObj.getTime()) && (dateStr.includes('-') || typeof dateValue === 'number')) {
        const day = String(dateObj.getDate()).padStart(2, '0');
        const month = String(dateObj.getMonth() + 1).padStart(2, '0');
        const hours = String(dateObj.getHours()).padStart(2, '0');
        const minutes = String(dateObj.getMinutes()).padStart(2, '0');

        if (compact) return `${day}/${month}`;
        return `${day}/${month} às ${hours}:${minutes}`;
    }

    if (compact) {
        return dateStr.split('/20')[0];
    }

    return dateStr;
};

document.addEventListener('DOMContentLoaded', () => {
    setTimeout(() => {
        const splash = document.getElementById('splash');
        if (splash) {
            splash.style.opacity = '0';
            setTimeout(() => {
                splash.style.display = 'none';
            }, 500);
        }
    }, 2000);

    fetchData();
    initBible();
    setupBranding();
    registerServiceWorker();
    setupInstallPrompt();
});

let deferredPrompt;
function setupInstallPrompt() {
    const installContainer = document.getElementById('install-container');
    const installBtn = document.getElementById('install-button');

    window.addEventListener('beforeinstallprompt', (e) => {
        // Previne que o Chrome mostre o prompt automático
        e.preventDefault();
        // Guarda o evento para disparar depois
        deferredPrompt = e;
        // Mostra o botão de instalação
        if (installContainer) installContainer.style.display = 'block';
    });

    if (installBtn) {
        installBtn.addEventListener('click', async () => {
            if (!deferredPrompt) return;
            // Mostra o prompt
            deferredPrompt.prompt();
            // Espera pela escolha do usuário
            const { outcome } = await deferredPrompt.userChoice;
            console.log(`User response to the install prompt: ${outcome}`);
            // Limpa o prompt
            deferredPrompt = null;
            // Esconde o botão
            if (installContainer) installContainer.style.display = 'none';
        });
    }

    window.addEventListener('appinstalled', () => {
        // Esconde o botão após instalação
        if (installContainer) installContainer.style.display = 'none';
        deferredPrompt = null;
        console.log('PWA was installed');
    });
}

function registerServiceWorker() {
    if ('serviceWorker' in navigator) {
        window.addEventListener('load', () => {
            navigator.serviceWorker.register('./sw.js').then(reg => {
                console.log('Service Worker registrado!');

                // Se houver um worker esperando, mostra a notificação
                if (reg.waiting) {
                    showUpdateNotification(reg.waiting);
                }

                // Escuta por novos workers instalando
                reg.addEventListener('updatefound', () => {
                    const newWorker = reg.installing;
                    newWorker.addEventListener('statechange', () => {
                        if (newWorker.state === 'installed' && navigator.serviceWorker.controller) {
                            showUpdateNotification(newWorker);
                        }
                    });
                });
            }).catch(err => console.log('Falha ao registrar Service Worker', err));
        });

        // Recarrega a página quando o novo Service Worker assume o controle
        let refreshing = false;
        navigator.serviceWorker.addEventListener('controllerchange', () => {
            if (!refreshing) {
                window.location.reload();
                refreshing = true;
            }
        });
    }
}

function showUpdateNotification(worker) {
    const notification = document.getElementById('update-notification');
    const btn = document.getElementById('update-button');

    if (notification && btn) {
        notification.style.display = 'block';
        btn.onclick = () => {
            worker.postMessage('SKIP_WAITING');
            notification.style.display = 'none';
        };
    }
}

function setupBranding(data = null) {
    const appName = data?.config?.NomeIgreja || "App Igrejas";
    const logoUrl = data?.config?.LogoUrl || "";

    // Atualiza nomes
    const splashName = document.getElementById('splash-app-name');
    const headerName = document.getElementById('header-app-name');
    if (splashName) splashName.innerText = appName;
    if (headerName) headerName.innerText = appName;

    // Atualiza Logos
    if (logoUrl) {
        const splashLogo = document.getElementById('splash-logo-placeholder');
        const headerLogo = document.getElementById('header-logo-placeholder');

        if (splashLogo) {
            splashLogo.innerHTML = `<img src="${logoUrl}" style="width:100px; height:100px; object-fit:contain; margin-bottom:20px; animation: pulse 2s infinite">`;
        }
        if (headerLogo) {
            headerLogo.innerHTML = `<img src="${logoUrl}">`;
        }
    }
}

function openLiveStream() {
    const liveUrl = window.appData?.config?.LinkAoVivo || "https://www.youtube.com/results?search_query=igreja+ao+vivo";
    window.open(liveUrl, '_blank');
}

function selectMood(mood) {
    // Navega para a tela da Bíblia (índice 2 na nav)
    navigateTo('bible', 2);

    // Switch para a aba de Devocional
    const tabs = document.querySelectorAll('#bible .tab-btn');
    if (tabs[1]) switchSubTab('bible-devotional', 'bible-content', tabs[1]);

    // Carrega o conteúdo baseado no humor
    loadDevotional(mood);
}

function navigateTo(targetId, elementOrIndex) {
    document.querySelectorAll('.nav-item').forEach(item => item.classList.remove('active'));

    if (elementOrIndex instanceof HTMLElement) {
        elementOrIndex.classList.add('active');
    } else if (typeof elementOrIndex === 'number') {
        const navItems = document.querySelectorAll('.nav-item');
        if (navItems[elementOrIndex]) navItems[elementOrIndex].classList.add('active');
    }

    document.querySelectorAll('.view').forEach(view => view.classList.remove('active'));
    const target = document.getElementById(targetId);
    if (target) target.classList.add('active');

    if (targetId === 'community') {
        showCommunitySection('ministries');
    }
}

function switchSubTab(showId, hideId, btn) {
    document.getElementById(showId).classList.add('active');
    document.getElementById(hideId).classList.remove('active');

    btn.parentNode.querySelectorAll('.tab-btn').forEach(b => b.classList.remove('active'));
    btn.classList.add('active');

    if (showId === 'bible-devotional') loadDevotional();
}

async function fetchData() {
    try {
        const response = await fetch(`${API_URL}?action=all`);
        const data = await response.json();
        window.appData = data;
        setupBranding(data);
        renderContent(data);
    } catch (error) {
        console.warn("Usando dados de simulação (API não encontrada ou URL placeholder).");
        window.appData = MOCK_DATA;
        renderContent(MOCK_DATA);
    }
}

function renderContent(data) {
    const bannerCarousel = document.getElementById('banner-carousel');
    const banners = data.banners || MOCK_DATA.banners;
    if (bannerCarousel) {
        bannerCarousel.innerHTML = banners.map(item => `
            <div class="carousel-item">
                <img src="${item.ImagemUrl}" alt="${item.Titulo}" onclick="applyZoomEffect(this)">
                <div class="carousel-caption">
                    <h2>${item.Titulo}</h2>
                    <p>${item.Subtitulo || ''}</p>
                </div>
            </div>
        `).join('');
        startAutoCycle(bannerCarousel);
    }

    const newsGrid = document.getElementById('news-grid');
    const noticias = data.noticias || MOCK_DATA.noticias;
    if (newsGrid) {
        newsGrid.innerHTML = noticias.map(item => `
            <div class="news-card">
                <img src="${item.ImagemUrl || 'https://via.placeholder.com/240x140/1A1A1A/FFD700?text=Igreja'}" alt="" style="width:100%; height:140px; object-fit:cover; border-radius:12px 12px 0 0">
                <div class="news-info" style="padding:12px">
                    <small class="gold-text" style="font-weight:bold">${formatDateBR(item.Data, true)}</small>
                    <h4 style="font-size:15px; margin:5px 0; color:var(--white)">${item.Titulo}</h4>
                    <p style="font-size:12px; opacity:0.8; line-height:1.4; color:var(--white)">${item.Descricao || ''}</p>
                </div>
            </div>
        `).join('');
    }

    const sermonsList = document.getElementById('sermons-list');
    const sermoes = data.sermoes || MOCK_DATA.sermoes;
    if (sermonsList) {
        sermonsList.innerHTML = sermoes.map(item => {
            const embedUrl = getEmbedUrl(item.VideoUrl);
            return `
                <div class="sermon-item" style="cursor:pointer" onclick="${embedUrl ? `window.open('${embedUrl}', '_blank')` : ''}">
                    <div class="sermon-thumb">
                        <img src="${item.ImagemUrl || 'https://via.placeholder.com/100/1A1A1A/FFD700?text=Play'}" alt="">
                        <i class="fas fa-play play-overlay"></i>
                    </div>
                    <div class="sermon-info">
                        <h4>${item.Titulo}</h4>
                        <p>${item.Pregador}</p>
                        <p style="font-size: 11px; opacity: 0.6">${formatDateBR(item.Data, true)}</p>
                    </div>
                </div>
            `;
        }).join('');
    }
}

function showCommunitySection(section, btn) {
    const content = document.getElementById('com-content');
    if (btn) {
        btn.parentNode.querySelectorAll('.tab-btn').forEach(b => b.classList.remove('active'));
        btn.classList.add('active');
    }

    if (section === 'ministries') {
        const list = window.appData?.ministerios || MOCK_DATA.ministerios;
        content.innerHTML = `<div class="section-container" style="padding-top:20px">` + list.map(m => `
            <div class="devotional-card" style="margin-bottom:16px; display:block; padding:0; overflow:hidden">
                <img src="${m.ImagemUrl || 'https://via.placeholder.com/400x120/1A1A1A/FFD700?text=Ministerio'}"
                     style="width: 100%; height: 120px; object-fit: cover; border-bottom: 1px solid rgba(255, 215, 0, 0.2);">
                <div style="padding:16px">
                    <h4 style="color:var(--gold); margin-bottom:8px">${m.Nome}</h4>
                    <p style="color:#eee; font-size:14px; margin-bottom:10px">${m.Descricao}</p>
                    <small style="opacity:0.8">Líder: ${m.Lider}</small>
                </div>
            </div>
        `).join('') + `</div>`;
    } else if (section === 'leadership') {
        const list = window.appData?.mensagemLider || MOCK_DATA.mensagemLider;
        content.innerHTML = `
            <div class="section-container" style="padding-top:20px; text-align:center">
                <i class="fas fa-user-tie" style="font-size:50px; color:var(--gold); margin-bottom:20px"></i>
                <h4 style="color:var(--gold)">Mensagens da Liderança</h4>
                <p style="margin-top:10px; opacity:0.8; margin-bottom:30px">Assista às palavras de fé dos nossos pastores.</p>

                <div style="text-align:left">
                    ${list.map(msg => {
                        const embedUrl = getEmbedUrl(msg.VideoUrl);
                        console.log("Carregando vídeo:", msg.VideoUrl, " -> Embed:", embedUrl);
                        return `
                            <div class="devotional-card" style="margin-bottom:24px; display:block; padding:0; overflow:hidden">
                                ${embedUrl ? `
                                    <iframe width="100%" style="aspect-ratio: 16/9; border:none"
                                        src="${embedUrl}"
                                        title="Video player"
                                        allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"
                                        referrerpolicy="strict-origin-when-cross-origin"
                                        allowfullscreen>
                                    </iframe>
                                ` : ''}
                                <div style="padding:15px; display:flex; align-items:center; gap:15px">
                                    ${msg.FotoUrl ? `<img src="${msg.FotoUrl}" style="width:50px; height:50px; border-radius:50%; object-fit:cover; border:2px solid var(--gold)">` : ''}
                                    <div>
                                        <h4 style="color:var(--gold); margin-bottom:2px">${msg.Mensagem}</h4>
                                        <p style="font-size:13px; font-weight:bold; margin-bottom:2px">${msg.Nome}</p>
                                        <small style="opacity:0.7; display:block">${msg.Cargo}</small>
                                    </div>
                                </div>
                            </div>
                        `;
                    }).join('')}
                </div>
            </div>
        `;
    } else if (section === 'events') {
        const list = (window.appData?.eventos && window.appData.eventos.length > 0)
                    ? window.appData.eventos
                    : (window.appData?.agenda || MOCK_DATA.eventos);

        content.innerHTML = `
            <div class="section-container" style="padding-top:20px">
                <h4 style="color:var(--gold); margin-bottom:20px; text-align:center">Agenda Semanal</h4>
                ${list.map(ev => {
                    const diaRaw = ev.Data || ev.Dia || "";
                    const titulo = ev.Titulo || ev.Evento || "";
                    const horaRaw = ev.Horario || ev.Hora || "";
                    const local = ev.Local || "";

                    // Formatação de Data e Hora no padrão Brasil
                    const dia = formatDateBR(diaRaw, true) || diaRaw;
                    const hora = (horaRaw && horaRaw.includes(':')) ? horaRaw : (formatDateBR(horaRaw) ? formatDateBR(horaRaw).split(' às ')[1] : horaRaw);

                    return `
                        <div class="devotional-card" style="margin-bottom:16px; border-left: 4px solid var(--gold); padding: 16px; display: flex; align-items: center; gap: 15px">
                            <div style="background: rgba(255, 215, 0, 0.1); width: 45px; height: 45px; border-radius: 10px; display: flex; align-items: center; justify-content: center">
                                <i class="fas fa-calendar-day" style="color:var(--gold); font-size: 20px"></i>
                            </div>
                            <div style="flex: 1">
                                <h4 style="margin: 0; color: var(--gold); font-size: 16px; font-weight: 700">${titulo}</h4>
                                <div style="display: flex; align-items: center; gap: 8px; margin-top: 4px; flex-wrap: wrap">
                                    <span style="color: #fff; font-size: 13px; font-weight: 500">${dia}</span>
                                    ${dia && (hora || local) ? `<span style="color: var(--gold); opacity: 0.5">•</span>` : ''}
                                    <span style="color: #fff; font-size: 13px">${hora}</span>
                                    ${hora && local ? `<span style="color: var(--gold); opacity: 0.5">•</span>` : ''}
                                    <span style="color: #ccc; font-size: 13px">${local}</span>
                                </div>
                            </div>
                        </div>
                    `;
                }).join('')}
            </div>
        `;

        if (!btn) {
            const tabs = document.querySelectorAll('#community .tab-btn');
            tabs.forEach(t => t.classList.remove('active'));
            if (tabs[2]) tabs[2].classList.add('active');
        }
    }
}

let bibleBooksData = [];
// Mapeamento para API Bolls.life (ID do livro e total de capítulos)
const BIBLE_BOOKS_FALLBACK = [
    { id: 1, name: "Gênesis", abbrev: "gn", chapters: 50 },
    { id: 2, name: "Êxodo", abbrev: "ex", chapters: 40 },
    { id: 3, name: "Levítico", abbrev: "lv", chapters: 27 },
    { id: 4, name: "Números", abbrev: "nm", chapters: 36 },
    { id: 5, name: "Deuteronômio", abbrev: "dt", chapters: 34 },
    { id: 6, name: "Josué", abbrev: "js", chapters: 24 },
    { id: 7, name: "Juízes", abbrev: "jz", chapters: 21 },
    { id: 8, name: "Rute", abbrev: "rt", chapters: 4 },
    { id: 9, name: "1 Samuel", abbrev: "1sm", chapters: 31 },
    { id: 10, name: "2 Samuel", abbrev: "2sm", chapters: 24 },
    { id: 11, name: "1 Reis", abbrev: "1rs", chapters: 22 },
    { id: 12, name: "2 Reis", abbrev: "2rs", chapters: 25 },
    { id: 13, name: "1 Crônicas", abbrev: "1cr", chapters: 29 },
    { id: 14, name: "2 Crônicas", abbrev: "2cr", chapters: 36 },
    { id: 15, name: "Esdras", abbrev: "ezr", chapters: 10 },
    { id: 16, name: "Neemias", abbrev: "ne", chapters: 13 },
    { id: 17, name: "Ester", abbrev: "et", chapters: 10 },
    { id: 18, name: "Jó", abbrev: "job", chapters: 42 },
    { id: 19, name: "Salmos", abbrev: "ps", chapters: 150 },
    { id: 20, name: "Provérbios", abbrev: "pr", chapters: 31 },
    { id: 21, name: "Eclesiastes", abbrev: "ec", chapters: 12 },
    { id: 22, name: "Cânticos", abbrev: "ct", chapters: 8 },
    { id: 23, name: "Isaías", abbrev: "is", chapters: 66 },
    { id: 24, name: "Jeremias", abbrev: "jr", chapters: 52 },
    { id: 25, name: "Lamentações", abbrev: "lm", chapters: 5 },
    { id: 26, name: "Ezequiel", abbrev: { pt: "ez" }, chapters: 48 },
    { id: 27, name: "Daniel", abbrev: "dn", chapters: 12 },
    { id: 28, name: "Oseias", abbrev: "os", chapters: 14 },
    { id: 29, name: "Joel", abbrev: "jl", chapters: 3 },
    { id: 30, name: "Amós", abbrev: "am", chapters: 9 },
    { id: 31, name: "Obadias", abbrev: "ob", chapters: 1 },
    { id: 32, name: "Jonas", abbrev: "jn", chapters: 4 },
    { id: 33, name: "Miqueias", abbrev: "mi", chapters: 7 },
    { id: 34, name: "Naum", abbrev: "na", chapters: 3 },
    { id: 35, name: "Habacuque", abbrev: "hb", chapters: 3 },
    { id: 36, name: "Sofonias", abbrev: "sf", chapters: 3 },
    { id: 37, name: "Ageu", abbrev: "ag", chapters: 2 },
    { id: 38, name: "Zacarias", abbrev: "zc", chapters: 14 },
    { id: 39, name: "Malaquias", abbrev: "ml", chapters: 4 },
    { id: 40, name: "Mateus", abbrev: "mt", chapters: 28 },
    { id: 41, name: "Marcos", abbrev: "mk", chapters: 16 },
    { id: 42, name: "Lucas", abbrev: "lk", chapters: 24 },
    { id: 43, name: "João", abbrev: "jn", chapters: 21 },
    { id: 44, name: "Atos", abbrev: "act", chapters: 28 },
    { id: 45, name: "Romanos", abbrev: "rm", chapters: 16 },
    { id: 46, name: "1 Coríntios", abbrev: "1co", chapters: 16 },
    { id: 47, name: "2 Coríntios", abbrev: "2co", chapters: 13 },
    { id: 48, name: "Gálatas", abbrev: "gl", chapters: 6 },
    { id: 49, name: "Efésios", abbrev: "ep", chapters: 6 },
    { id: 50, name: "Filipenses", abbrev: "ph", chapters: 4 },
    { id: 51, name: "Colossenses", abbrev: "cl", chapters: 4 },
    { id: 52, name: "1 Tessalonicenses", abbrev: "1ts", chapters: 5 },
    { id: 53, name: "2 Tessalonicenses", abbrev: "2ts", chapters: 3 },
    { id: 54, name: "1 Timóteo", abbrev: "1tm", chapters: 6 },
    { id: 55, name: "2 Timóteo", abbrev: "2tm", chapters: 4 },
    { id: 56, name: "Tito", abbrev: "tt", chapters: 3 },
    { id: 57, name: "Filemom", abbrev: "phm", chapters: 1 },
    { id: 58, name: "Hebreus", abbrev: "hb", chapters: 13 },
    { id: 59, name: "Tiago", abbrev: "tg", chapters: 5 },
    { id: 60, name: "1 Pedro", abbrev: "1pe", chapters: 5 },
    { id: 61, name: "2 Pedro", abbrev: "2pe", chapters: 3 },
    { id: 62, name: "1 João", abbrev: "1jn", chapters: 5 },
    { id: 63, name: "2 João", abbrev: "2jn", chapters: 1 },
    { id: 64, name: "3 João", abbrev: "3jn", chapters: 1 },
    { id: 65, name: "Judas", abbrev: "jd", chapters: 1 },
    { id: 66, name: "Apocalipse", abbrev: "re", chapters: 22 }
];

async function initBible() {
    const versionSelector = document.getElementById('version-selector');
    const bookSelector = document.getElementById('book-selector');
    const chapterSelector = document.getElementById('chapter-selector');
    const textContainer = document.getElementById('bible-text');

    if (!bookSelector || !chapterSelector || !textContainer) return;

    // Atualiza versões para os códigos da Bolls.life
    versionSelector.innerHTML = `
        <option value="NVIPT">NVI</option>
        <option value="ARAV">ARA</option>
        <option value="ARC">ARC</option>
        <option value="KJV">KJV</option>
    `;

    bibleBooksData = BIBLE_BOOKS_FALLBACK;

    // Popula Livros
    bookSelector.innerHTML = bibleBooksData.map(book =>
        `<option value="${book.id}">${book.name}</option>`
    ).join('');

    // Listeners
    versionSelector.addEventListener('change', () => loadVerses());
    bookSelector.addEventListener('change', () => updateChapters());
    chapterSelector.addEventListener('change', () => loadVerses());

    updateChapters();
}

function updateChapters() {
    const bookSelector = document.getElementById('book-selector');
    const chapterSelector = document.getElementById('chapter-selector');
    const selectedId = parseInt(bookSelector.value);

    const book = bibleBooksData.find(b => b.id === selectedId);
    if (book) {
        let options = "";
        for (let i = 1; i <= book.chapters; i++) {
            options += `<option value="${i}">${i}</option>`;
        }
        chapterSelector.innerHTML = options;
        loadVerses();
    }
}

async function loadVerses() {
    const textContainer = document.getElementById('bible-text');
    const version = document.getElementById('version-selector').value;
    const bookId = document.getElementById('book-selector').value;
    const chapter = document.getElementById('chapter-selector').value;

    textContainer.innerHTML = "<p style='text-align:center; opacity:0.5; padding: 20px;'>Buscando palavra sagrada...</p>";

    try {
        // API Bolls.life: Versão / ID Livro / Capítulo
        const response = await fetch(`https://bolls.life/get-chapter/${version}/${bookId}/${chapter}/`);

        if (!response.ok) throw new Error("Erro na conexão com o servidor da Bíblia.");

        const verses = await response.json();

        if (verses && verses.length > 0) {
            textContainer.innerHTML = verses.map(v => `
                <p style="margin-bottom:15px; line-height:1.6">
                    <span style="color:var(--gold); font-weight:bold; margin-right:10px; font-size:0.9em">${v.verse}</span>
                    ${v.text}
                </p>
            `).join('');
        } else {
            textContainer.innerHTML = "<p style='padding:20px; text-align:center'>Nenhum versículo encontrado.</p>";
        }
    } catch (error) {
        console.error("Erro Bíblia:", error);
        textContainer.innerHTML = `
            <div style="text-align:center; padding:20px">
                <p style="color:var(--gold)">Não foi possível carregar este capítulo.</p>
                <p style="font-size:12px; opacity:0.7; margin-top:10px">Tente trocar a versão ou verifique sua internet.</p>
                <button class="btn-gold" style="margin-top:20px; padding:8px 15px; font-size:12px" onclick="loadVerses()">Tentar Novamente</button>
            </div>
        `;
    }
    textContainer.scrollTop = 0;
}

const DEVOTIONAL_VERSES = {
    'Triste': [
        { title: "O Consolo que Vem do Alto", ref: "Salmos 34:18", text: "Perto está o Senhor dos que têm o coração quebrantado e salva os de espírito oprimido." },
        { title: "Deus Enxugará suas Lágrimas", ref: "Apocalipse 21:4", text: "Ele enxugará dos seus olhos toda lágrima. Não haverá mais morte, nem tristeza, nem choro, nem dor." },
        { title: "Refúgio e Fortaleza", ref: "Salmos 46:1", text: "Deus é o nosso refúgio e a nossa fortaleza, auxílio sempre presente na adversidade." }
    ],
    'Cansado': [
        { title: "Descanso para a Alma", ref: "Mateus 11:28", text: "Vinde a mim, todos os que estais cansados e oprimidos, e eu vos aliviarei." },
        { title: "Forças Renovadas", ref: "Isaías 40:31", text: "Mas aqueles que esperam no Senhor renovam as suas forças. Voam alto como águias; correm e não se fatigam." },
        { title: "Socorro Bem Presente", ref: "Salmos 121:1-2", text: "Elevo os meus olhos para os montes; de onde vem o meu socorro? O meu socorro vem do Senhor, que fez o céu e a terra." }
    ],
    'Grato': [
        { title: "O Sacrifício de Louvor", ref: "1 Tessalonicenses 5:18", text: "Em tudo dai graças, porque esta é a vontade de Deus em Cristo Jesus para convosco." },
        { title: "A Bondade do Senhor", ref: "Salmos 107:1", text: "Deem graças ao Senhor, porque ele é bom; o seu amor dura para sempre." },
        { title: "Louvor de Coração", ref: "Salmos 103:1", text: "Bendiga ao Senhor a minha alma! Bendiga ao seu santo nome todo o meu ser!" }
    ],
    'Feliz': [
        { title: "A Alegria do Senhor", ref: "Neemias 8:10", text: "Não vos entristeçais, porque a alegria do Senhor é a vossa força." },
        { title: "Coração Alegre", ref: "Provérbios 15:13", text: "O coração alegre aformoseia o rosto, mas pela dor do coração o espírito se abate." },
        { title: "Regozijo Constante", ref: "Filipenses 4:4", text: "Alegrem-se sempre no Senhor. Novamente direi: Alegrem-se!" }
    ],
    'default': [
        { title: "O Renovo das Misericórdias", ref: "Lamentações 3:22-23", text: "As misericórdias do Senhor são a causa de não sermos consumidos, porque as suas compaixões não têm fim; renovam-se cada manhã." }
    ]
};

function loadDevotional(mood = null) {
    const detail = document.getElementById('devotional-detail');
    if (!detail) return;

    const verses = DEVOTIONAL_VERSES[mood] || DEVOTIONAL_VERSES['default'];
    const randomVerse = verses[Math.floor(Math.random() * verses.length)];

    detail.innerHTML = `
        <div class="section-container" style="padding-top:20px">
            ${mood ? `<small class="gold-text">Sinto-me ${mood}</small>` : ''}
            <h2 style="color:var(--gold); margin-top:5px">${randomVerse.title}</h2>
            <p style="color:var(--light-grey); margin-bottom:20px">${randomVerse.ref}</p>
            <p style="line-height:1.6; font-size:1.1em; font-style:italic">"${randomVerse.text}"</p>
            <button class="btn-outline" style="margin-top:30px" onclick="shareDevotional('${randomVerse.title.replace(/'/g, "\\'")}', '${randomVerse.ref}', '${randomVerse.text.replace(/'/g, "\\'")}')">
                Compartilhar
            </button>
        </div>
    `;
}

function shareDevotional(title, reference, text) {
    const shareText = `${title}\n\n"${text}"\n\n${reference}`;

    if (navigator.share) {
        navigator.share({
            title: title,
            text: shareText
        }).catch(err => console.log('Erro ao compartilhar:', err));
    } else {
        navigator.clipboard.writeText(shareText).then(() => {
            alert("Devocional copiado para a área de transferência!");
        });
    }
}

function openGivingModal() {
    document.getElementById('giving-modal').style.display = 'block';
    switchGivingTab('dizimo');
}
function closeGivingModal() { document.getElementById('giving-modal').style.display = 'none'; }

function switchGivingTab(type) {
    currentGivingType = type;
    const content = document.getElementById('giving-form-content');
    const tabDizimo = document.getElementById('tab-dizimo');
    const tabOferta = document.getElementById('tab-oferta');

    tabDizimo.classList.remove('active');
    tabOferta.classList.remove('active');

    if (type === 'dizimo') {
        tabDizimo.classList.add('active');
        content.innerHTML = `
            <h4>Informar Dízimo</h4>
            <input type="text" placeholder="Nome Completo" id="give-name">
            <input type="text" placeholder="CPF ou Telefone" id="give-ident">
            <input type="number" placeholder="Valor R$" id="give-value">
            <div style="margin-top:10px; text-align:left">
                <label style="font-size:12px; color:var(--gold); display:block; margin-bottom:5px">Anexar Comprovante (Opcional)</label>
                <input type="file" id="give-receipt" accept="image/*,application/pdf" style="font-size:12px; border:none; padding:0">
            </div>
            <button class="btn-outline" style="margin-top:15px" onclick="submitGiving()">Informar Envio</button>
        `;
    } else {
        tabOferta.classList.add('active');
        content.innerHTML = `
            <h4>Informar Oferta</h4>
            <input type="text" placeholder="Nome (Opcional)" id="give-name">
            <input type="number" placeholder="Valor R$" id="give-value">
            <button class="btn-outline" onclick="submitGiving()">Informar Envio</button>
        `;
    }
}

function copyPix() {
    const key = document.getElementById('pix-key').innerText;
    navigator.clipboard.writeText(key);
    alert("Chave PIX copiada!");
}

async function submitGiving() {
    const name = document.getElementById('give-name').value;
    const value = document.getElementById('give-value').value;
    const ident = document.getElementById('give-ident') ? document.getElementById('give-ident').value : "N/A";
    const fileInput = document.getElementById('give-receipt');

    if(currentGivingType === 'dizimo' && (!name || !value || !ident)) {
        return alert("Preencha todos os campos do Dízimo");
    }
    if(currentGivingType === 'oferta' && !value) {
        return alert("Informe o valor da oferta");
    }

    const payload = {
        type: 'giving',
        subtipo: currentGivingType === 'dizimo' ? 'Dízimo' : 'Oferta',
        nome: name || "Anônimo",
        identificacao: ident,
        valor: value,
        data: new Date().toLocaleDateString(),
        fileData: null,
        fileName: null
    };

    if (fileInput && fileInput.files && fileInput.files[0]) {
        const file = fileInput.files[0];
        try {
            const base64 = await new Promise((resolve, reject) => {
                const reader = new FileReader();
                reader.onload = () => resolve(reader.result.split(',')[1]);
                reader.onerror = error => reject(error);
                reader.readAsDataURL(file);
            });
            payload.fileData = base64;
            payload.fileName = file.name;
        } catch (e) {
            console.error("Erro ao ler arquivo:", e);
            return alert("Erro ao processar o arquivo. Tente novamente.");
        }
    }

    try {
        await fetch(API_URL, {
            method: 'POST',
            mode: 'no-cors',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });
        alert("Sua contribuição de " + payload.subtipo + " foi enviada!");
        closeGivingModal();
    } catch(e) {
        console.error(e);
        alert("Erro ao enviar. Tente novamente.");
    }
}

async function submitPrayer() {
    const name = document.getElementById('p-name').value;
    const reason = document.getElementById('p-reason').value;
    const msg = document.getElementById('p-msg').value;

    if(!name || !reason || !msg) return alert("Por favor, preencha seu nome, selecione o motivo e escreva seu pedido.");

    const payload = {
        type: 'prayer',
        nome: name,
        motivo: reason,
        mensagem: msg,
        data: new Date().toLocaleString()
    };

    try {
        await fetch(API_URL, {
            method: 'POST',
            mode: 'no-cors',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });
        alert("Pedido de oração enviado com sucesso!");
        document.getElementById('p-name').value = "";
        document.getElementById('p-reason').value = "";
        document.getElementById('p-msg').value = "";
    } catch (e) {
        console.error(e);
        alert("Erro ao enviar pedido de oração.");
    }
}

function applyZoomEffect(img) {
    img.classList.add('zoomed');
    setTimeout(() => {
        img.classList.remove('zoomed');
    }, 2000);
}

function startAutoCycle(carousel) {
    if (bannerInterval) clearInterval(bannerInterval);
    bannerInterval = setInterval(() => {
        const itemWidth = carousel.querySelector('.carousel-item').offsetWidth + 10;
        const maxScroll = carousel.scrollWidth - carousel.offsetWidth;

        if (carousel.scrollLeft >= maxScroll - 5) {
            carousel.scrollTo({ left: 0, behavior: 'smooth' });
        } else {
            carousel.scrollBy({ left: itemWidth, behavior: 'smooth' });
        }
    }, 5000);
}

if ('serviceWorker' in navigator) {
    window.addEventListener('load', () => {
        // navigator.serviceWorker.register('/sw.js');
    });
}

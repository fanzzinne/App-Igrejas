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
    eventos: []
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
            navigator.serviceWorker.register('./sw.js')
                .then(reg => console.log('Service Worker registrado!', reg))
                .catch(err => console.log('Falha ao registrar Service Worker', err));
        });
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
        const list = window.appData?.eventos && window.appData.eventos.length > 0
                    ? window.appData.eventos
                    : (window.appData?.noticias || MOCK_DATA.noticias);

        content.innerHTML = `<div class="section-container" style="padding-top:20px">` + list.map(ev => `
            <div class="devotional-card" style="margin-bottom:12px; border-left: 4px solid var(--gold)">
                <div style="display:flex; justify-content:space-between; align-items:center; width:100%">
                    <div>
                        <small class="gold-text" style="font-weight:bold">${formatDateBR(ev.Data)}</small>
                        <h4 style="margin:5px 0">${ev.Titulo}</h4>
                        <p style="color:#aaa; font-size:13px">${ev.Descricao || ''}</p>
                    </div>
                    <i class="fas fa-calendar-check" style="color:var(--gold); opacity:0.5"></i>
                </div>
            </div>
        `).join('') + `</div>`;

        if (!btn) {
            const tabs = document.querySelectorAll('#community .tab-btn');
            tabs.forEach(t => t.classList.remove('active'));
            if (tabs[2]) tabs[2].classList.add('active');
        }
    }
}

const BIBLE_BOOKS = [
    "Gênesis", "Êxodo", "Levítico", "Números", "Deuteronômio", "Josué", "Juízes", "Rute", "1 Samuel", "2 Samuel",
    "1 Reis", "2 Reis", "1 Crônicas", "2 Crônicas", "Esdras", "Neemias", "Ester", "Jó", "Salmos", "Provérbios",
    "Eclesiastes", "Cânticos", "Isaías", "Jeremias", "Lamentações", "Ezequiel", "Daniel", "Oseias", "Joel", "Amós",
    "Obadias", "Jonas", "Miqueias", "Naum", "Habacuque", "Sofonias", "Ageu", "Zacarias", "Malaquias",
    "Mateus", "Marcos", "Lucas", "João", "Atos", "Romanos", "1 Coríntios", "2 Coríntios", "Gálatas", "Efésios",
    "Filipenses", "Colossenses", "1 Tessalonicenses", "2 Tessalonicenses", "1 Timóteo", "2 Timóteo", "Tito", "Filemom",
    "Hebreus", "Tiago", "1 Pedro", "2 Pedro", "1 João", "2 João", "3 João", "Judas", "Apocalipse"
];

function initBible() {
    const bookSelector = document.getElementById('book-selector');
    const chapterSelector = document.getElementById('chapter-selector');
    const textContainer = document.getElementById('bible-text');

    if (!bookSelector || !chapterSelector || !textContainer) return;

    // Popula Livros
    bookSelector.innerHTML = BIBLE_BOOKS.map(book => `<option value="${book}">${book}</option>`).join('');

    // Listener para mudar capítulos (simulação)
    bookSelector.addEventListener('change', () => updateChapters());
    chapterSelector.addEventListener('change', () => loadVerses());

    updateChapters();
}

function updateChapters() {
    const chapterSelector = document.getElementById('chapter-selector');
    // Simula quantidade de capítulos
    const chapters = Math.floor(Math.random() * 20) + 10;
    let options = "";
    for(let i=1; i<=chapters; i++) {
        options += `<option value="${i}">${i}</option>`;
    }
    chapterSelector.innerHTML = options;
    loadVerses();
}

function loadVerses() {
    const textContainer = document.getElementById('bible-text');
    const book = document.getElementById('book-selector').value;
    const chapter = document.getElementById('chapter-selector').value;

    let verses = "";
    for(let i=1; i<=20; i++) {
        verses += `<p style="margin-bottom:15px; line-height:1.6">
            <span style="color:var(--gold); font-weight:bold; margin-right:10px; font-size:0.9em">${i}</span>
            Exemplo de versículo para o livro de ${book}, capítulo ${chapter}. A palavra do Senhor permanece para sempre.
        </p>`;
    }
    textContainer.innerHTML = verses;
    textContainer.scrollTop = 0;
}

function loadDevotional(mood = null) {
    const detail = document.getElementById('devotional-detail');
    if (!detail) return;

    let title = "O Renovo das Misericórdias";
    let reference = "Lamentações 3:22-23";
    let text = "As misericórdias do Senhor são a causa de não sermos consumidos, porque as suas compaixões não têm fim; renovam-se cada manhã. Grande é a tua fidelidade.";

    if (mood === 'Triste') {
        title = "O Consolo que Vem do Alto";
        reference = "Salmos 34:18";
        text = "Perto está o Senhor dos que têm o coração quebrantado e salva os de espírito oprimido. Não se desespere, Ele cuida de cada detalhe da sua dor.";
    } else if (mood === 'Cansado') {
        title = "Descanso para a Alma";
        reference = "Mateus 11:28";
        text = "Vinde a mim, todos os que estais cansados e oprimidos, e eu vos aliviarei. Deixe seus fardos aos pés da cruz hoje.";
    } else if (mood === 'Grato') {
        title = "O Sacrifício de Louvor";
        reference = "1 Tessalonicenses 5:18";
        text = "Em tudo dai graças, porque esta é a vontade de Deus em Cristo Jesus para convosco. Seu coração grato abre portas para o sobrenatural.";
    } else if (mood === 'Feliz') {
        title = "A Alegria do Senhor";
        reference = "Neemias 8:10";
        text = "Não vos entristeçais, porque a alegria do Senhor é a vossa força. Celebre as bênçãos que Ele tem derramado sobre sua vida!";
    }

    detail.innerHTML = `
        <div class="section-container" style="padding-top:20px">
            ${mood ? `<small class="gold-text">Sinto-me ${mood}</small>` : ''}
            <h2 style="color:var(--gold); margin-top:5px">${title}</h2>
            <p style="color:var(--light-grey); margin-bottom:20px">${reference}</p>
            <p style="line-height:1.6">${text}</p>
            <button class="btn-outline" style="margin-top:30px">Compartilhar</button>
        </div>
    `;
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

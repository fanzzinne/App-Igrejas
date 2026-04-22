# Project Plan

Desenvolver a versão Web App solicitada, composta por index.html, app.js e Google Apps Script. O sistema deve usar o Google Sheets como banco de dados para gerenciar conteúdos (Banners, Eventos, etc.) e processar envios de oração e comprovantes de dízimo. O design deve ser luxuoso em preto e dourado.

## Project Brief

### Project Brief: Web App Igrejas (Google Sheets Backend)

#### Objective
Build a Web Application (PWA) for a church with a premium Black and Gold theme, using Google Sheets as the database and Google Apps Script as the backend.

#### Features
1. **Dynamic Home Dashboard**: Carousel, weekly events, news, live button, mood bar, circular quick actions.
2. **Sermon/Media Library**: Integrated library fetching data from Sheets.
3. **Bible & Devotional**: Daily tools.
4. **Prayer Request Form**: Integrated with Apps Script to send emails.
5. **Digital Giving**: PIX/Bank info with receipt upload/submission via Apps Script.
6. **Ministries & Leadership**: Displaying groups and messages from leaders.
7. **PWA Support**: Offline capabilities and installation trigger.
8. **Theme**: Black background with Gold accents (#FFD700).

#### Technical Stack
- Frontend: HTML5, CSS3 (Vanilla or Tailwind), JavaScript (ES6).
- Backend: Google Apps Script (Web App).
- Database: Google Sheets.
- Icons: Font Awesome or Material Icons.

## Implementation Steps
**Total Duration:** 26m 20s

### Task_1_Setup_and_Home_UI: Setup Material 3 Black & Gold theme, Edge-to-Edge, Bottom Navigation, and the Home Dashboard UI including Carousel and Quick Actions.
- **Status:** COMPLETED
- **Updates:** Implemented Material 3 Black & Gold theme, Edge-to-Edge support, and Bottom Navigation. Created the Home Dashboard with a banner carousel, circular Quick Actions, Humor Bar, horizontal News grid, and the required footer. Adaptive app icon was also generated. Project builds successfully.
- **Acceptance Criteria:**
  - App uses a Black and Gold Material 3 theme.
  - Edge-to-Edge is implemented.
  - Bottom Navigation navigates between placeholder screens.
  - Home Dashboard displays a carousel and quick action buttons.
  - Build passes successfully.
- **Duration:** 3m 35s

### Task_2_Media_and_Spiritual_Growth: Implement the Sermon Library and Spiritual Growth tools (Bible and Devotionals) using MVVM, Room for local storage, and Retrofit for media data.
- **Status:** COMPLETED
- **Updates:** Implemented the Sermon Library with MVVM and Retrofit (mocked). Developed the Bible reader and Daily Devotional screens with a TabRow interface. Configured Room database with entities for Devotionals and Bible Chapters for local caching. Integrated these features into the existing navigation and updated the Home screen with a Devotional shortcut. Maintain the Black and Gold aesthetic.
- **Acceptance Criteria:**
  - Sermon Library displays a list of media content.
  - Bible and Devotional sections are accessible and display content.
  - Room database is integrated for growth tools.
  - Retrofit is configured for fetching sermon data.
  - App does not crash during navigation.
- **Duration:** 2m 28s

### Task_3_Community_and_Interactivity: Build the Ministries list, Leader's Area, Prayer Request form with email integration, and the Digital Giving section with Pix and receipt submission.
- **Status:** COMPLETED
- **Updates:** Implemented Ministries list, Leader's Area, and Prayer Request form with email intent and category selection. Built the Digital Giving screen with Pix copy-to-clipboard and receipt submission via email. Integrated these features into the 'Community' tab using a TabRow and updated Home screen Quick Actions for direct navigation. All features follow the Black and Gold Material 3 theme and MVVM/Repository pattern.
- **Acceptance Criteria:**
  - Ministries and Leader's Area show relevant church information.
  - Prayer Request form validates input and triggers email intent.
  - Digital Giving screen displays Pix info and allows receipt attachment.
  - App follows MVVM and Repository patterns.
  - App does not crash.
- **Duration:** 3m 28s

### Task_4_Polish_and_Verification: Refine the UI aesthetic, add the Footer, generate an adaptive app icon, and perform a final verification of all features and stability.
- **Status:** COMPLETED
- **Updates:** Completed final UI polish and verification. Added the required footer to all main screens. Refined the Black and Gold theme consistency. Verified adaptive app icon and navigation stability. The app builds successfully and all features (Home, Media, Bible, Community, Giving, Prayer) are functional with proper intents.
- **Acceptance Criteria:**
  - Footer is present on all main screens.
  - Adaptive app icon is created and matches the theme.
  - UI strictly follows the Black and Gold premium aesthetic.
  - Application is stable with no crashes.
  - All existing tests pass.
  - Final build pass.
- **Duration:** 14m 6s

### Task_5_Backend_Google_Sheets: Implement the Google Apps Script backend and Google Sheets database to manage dynamic content (Banners, Events, Sermons) and process form submissions.
- **Status:** COMPLETED
- **Updates:** Google Apps Script backend implemented with doGet (to fetch all content categories) and doPost (to handle prayer requests and giving receipts). Google Sheets structure defined with all required sheets (Banners, Events, Sermons, etc.). API handles JSON responses and includes email notification logic.
- **Acceptance Criteria:**
  - Google Apps Script handles GET requests to return JSON data from Sheets.
  - Google Apps Script handles POST requests for prayer requests and dízimo receipts.
  - Google Sheets is structured with dedicated sheets for app content.
  - API integration code is ready for the Android app.
- **Duration:** 25s

### Task_6_Web_Version_and_Sync: Develop the Web App frontend (index.html, app.js, CSS) and update the Android app to fetch live data from the Sheets API. Perform final run and verify.
- **Status:** COMPLETED
- **Updates:** Implementado suporte PWA completo. Criados manifest.json e sw.js. Adicionado botão de instalação luxuoso que aparece dinamicamente no Android e Desktop via evento beforeinstallprompt. Registro do Service Worker configurado no app.js.
- **Acceptance Criteria:**
  - index.html, app.js, and CSS are implemented with a Black and Gold theme.
  - Android app displays live data from Google Sheets instead of mocks.
  - Prayer and Giving forms successfully submit data to the backend.
  - Make sure all existing tests pass, build pass and app does not crash.
  - Critic_agent verifies application stability and requirement alignment.
- **Duration:** 2m 18s


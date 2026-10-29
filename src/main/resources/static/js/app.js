import { initTheme } from './modules/theme.js';
import { initNavigation } from './modules/navigation.js';
import { initForms } from './modules/forms.js';
import { initSearch } from './modules/search.js';
import { initAdmin } from './modules/admin.js';
import { initProfileMedia } from './modules/profile-media.js';
import { initAccessibility } from './modules/accessibility.js';

// Cada m�dulo verifica por conta pr�pria se os elementos que precisa
// existem na p�gina atual. Nenhum m�dulo deve lan�ar erro quando o
// componente correspondente n�o estiver presente.
initTheme();
initNavigation();
initForms();
initSearch();
initAdmin();
initProfileMedia();
initAccessibility();

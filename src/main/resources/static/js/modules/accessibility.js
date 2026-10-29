// Complementa os erros renderizados pelo servidor; a valida��o continua no backend.
export function initAccessibility() {
    document.querySelectorAll('form').forEach((form, formIndex) => {
        const errors = [...form.querySelectorAll('.form-field__error')].filter(e => e.textContent.trim());
        if (!errors.length) return;
        const summary = document.createElement('div');
        summary.className = 'error-summary';
        summary.tabIndex = -1;
        summary.setAttribute('role', 'alert');
        const heading = document.createElement('h2');
        heading.id = `error-summary-${formIndex}`;
        heading.textContent = 'Confira os campos indicados';
        summary.setAttribute('aria-labelledby', heading.id);
        summary.append(heading);
        const list = document.createElement('ul');
        errors.forEach(error => {
            const field = error.closest('.form-field')?.querySelector('input, textarea, select');
            if (!field?.id) return;
            const item = document.createElement('li');
            const link = document.createElement('a');
            link.href = `#${field.id}`;
            const label = [...field.labels || []].map(e => e.textContent.trim()).join(' ');
            link.textContent = `${label}: ${error.textContent.trim()}`;
            link.addEventListener('click', () => field.focus());
            item.append(link); list.append(item);
        });
        summary.append(list); form.prepend(summary);
    });
    const firstError = document.querySelector('.error-summary, .alert-error');
    if (firstError) { firstError.tabIndex = -1; firstError.focus(); }
    document.querySelectorAll('.table-wrap').forEach((wrap, index) => {
        wrap.tabIndex = 0;
        wrap.setAttribute('role', 'region');
        wrap.setAttribute('aria-label', `Tabela ${index + 1}. Use as setas para rolar horizontalmente.`);
    });
}

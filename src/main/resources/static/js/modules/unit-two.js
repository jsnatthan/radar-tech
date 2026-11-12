export function initUnitTwo() {
    initShareLink();
    initPublicationPreview();
}

function initShareLink() {
    const group = document.querySelector('[data-share-job]');
    if (!group) return;

    const field = group.querySelector('input');
    const button = group.querySelector('[data-copy-job]');
    const status = group.querySelector('[data-copy-status]');
    field.value = new URL(field.value, window.location.href).href;
    button.hidden = false;
    button.addEventListener('click', async () => {
        try {
            await navigator.clipboard.writeText(field.value);
            status.textContent = 'Link copiado.';
        } catch {
            field.focus();
            field.select();
            status.textContent = 'Link selecionado. Use Ctrl+C ou a opção Copiar.';
        }
    });
}

function initPublicationPreview() {
    const form = document.getElementById('publication-form');
    const dialog = document.getElementById('publication-preview');
    if (!form || !dialog || typeof dialog.showModal !== 'function') return;

    const review = form.querySelector('[data-review-publication]');
    const fields = dialog.querySelector('[data-preview-fields]');
    const location = form.querySelector('[data-location-field]');
    const city = form.querySelector('#post-city');
    let confirmed = false;
    review.textContent = 'Revisar anúncio';
    const requireCity = () => {
        city.required = location.value !== '' && location.value !== 'REMOTE';
    };
    location.addEventListener('change', requireCity);
    requireCity();

    form.addEventListener('submit', event => {
        if (confirmed) {
            review.disabled = true;
            return;
        }
        event.preventDefault();
        fields.replaceChildren();
        for (const field of form.querySelectorAll('input:not([type="hidden"]), select, textarea')) {
            const value = field instanceof HTMLSelectElement
                ? field.selectedOptions[0]?.textContent
                : field.value.trim();
            if (!value || !field.labels?.length) continue;
            const label = document.createElement('dt');
            const answer = document.createElement('dd');
            label.textContent = field.labels[0].textContent;
            answer.textContent = value;
            fields.append(label, answer);
        }
        dialog.showModal();
        dialog.querySelector('h2').focus();
    });

    dialog.querySelector('[data-edit-publication]').addEventListener('click', () => dialog.close());
    dialog.querySelector('[data-confirm-publication]').addEventListener('click', () => {
        dialog.close();
        confirmed = true;
        form.requestSubmit(review);
        confirmed = false;
    });
}

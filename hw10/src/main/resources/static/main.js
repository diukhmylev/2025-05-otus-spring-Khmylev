const api = "/api";
let authors = [], genres = [], books = [];

async function fetchJson(url, options) {
    const r = await fetch(url, options);
    if (!r.ok && r.status !== 204) {
        const text = await r.text().catch(() => '');
        console.error('HTTP error', r.status, r.statusText, 'URL:', url, 'Body:', text);
        throw new Error(`HTTP ${r.status}: ${r.statusText}`);
    }
    if (r.status === 204) return null;
    return r.json();
}

function authorNameById(authorId) {
    return authors.find(a => a.id === authorId)?.fullName || '';
}
function genreNamesByIds(genreIds) {
    if (!Array.isArray(genreIds)) return [];
    const byId = new Map(genres.map(g => [g.id, g.name]));
    return genreIds.map(id => byId.get(id)).filter(Boolean);
}

async function refreshAll() {
    try {
        const [a, g, b] = await Promise.all([
            fetchJson(`${api}/authors`).catch(e => { console.warn('authors failed', e); return []; }),
            fetchJson(`${api}/genres`).catch(e => { console.warn('genres failed', e); return []; }),
            fetchJson(`${api}/books`).catch(e => { console.warn('books failed', e); return []; })
        ]);
        authors = Array.isArray(a) ? a : [];
        genres  = Array.isArray(g) ? g : [];
        books   = Array.isArray(b) ? b : [];
        renderBooks();
        renderAuthors();
        renderGenres();
        renderComments();
        updateSelects();
    } catch (e) {
        console.error('refreshAll failed', e);
        alert('Failed to load data. See console for details.');
    }
}

function renderBooks() {
    const tb = document.querySelector("#bookTable tbody");
    if (!tb) return;
    tb.innerHTML = "";

    books.forEach(b => {
        const row = tb.insertRow();

        const titleCell = row.insertCell();
        titleCell.textContent = b.title || '';
        titleCell.style.cursor = 'pointer';
        titleCell.title = 'Click to select this book for the comment form';
        titleCell.onclick = () => {
            const sel = document.getElementById('commentBook');
            if (sel) sel.value = b.id;
        };

        const authorCell = row.insertCell();
        const authorText = b.authorFullName ? b.authorFullName : (b.authorId ? authorNameById(b.authorId) : '');
        authorCell.textContent = authorText;

        let genreText = '';
        if (Array.isArray(b.genreNames) && b.genreNames.length) {
            genreText = b.genreNames.join(', ');
        } else if (Array.isArray(b.genreIds) && b.genreIds.length) {
            genreText = genreNamesByIds(b.genreIds).join(', ');
        }
        row.insertCell().textContent = genreText;

        const commentsText = Array.isArray(b.commentTexts) && b.commentTexts.length
            ? b.commentTexts.join('; ')
            : '';
        row.insertCell().textContent = commentsText;

        const actions = document.createElement("td");
        const editBtn = document.createElement("button");
        editBtn.textContent = "Edit";
        editBtn.onclick = () => editBook(b.id);
        const delBtn = document.createElement("button");
        delBtn.textContent = "Delete";
        delBtn.onclick = () => deleteBook(b.id);
        actions.append(editBtn, delBtn);
        row.appendChild(actions);
    });
}

function renderAuthors() {
    const tb = document.querySelector("#authorTable tbody");
    if (!tb) return;
    tb.innerHTML = "";
    authors.forEach(a => {
        const row = tb.insertRow();
        row.insertCell().textContent = a.fullName || '';
        const actions = document.createElement("td");
        const editBtn = document.createElement("button");
        editBtn.textContent = "Edit";
        editBtn.onclick = () => editAuthor(a.id);
        const delBtn = document.createElement("button");
        delBtn.textContent = "Delete";
        delBtn.onclick = () => deleteAuthor(a.id);
        actions.append(editBtn, delBtn);
        row.appendChild(actions);
    });
}

function renderGenres() {
    const tb = document.querySelector("#genreTable tbody");
    if (!tb) return;
    tb.innerHTML = "";
    genres.forEach(g => {
        const row = tb.insertRow();
        row.insertCell().textContent = g.name || '';
        const actions = document.createElement("td");
        const editBtn = document.createElement("button");
        editBtn.textContent = "Edit";
        editBtn.onclick = () => editGenre(g.id);
        const delBtn = document.createElement("button");
        delBtn.textContent = "Delete";
        delBtn.onclick = () => deleteGenre(g.id);
        actions.append(editBtn, delBtn);
        row.appendChild(actions);
    });
}

function renderComments() {
    const tb = document.querySelector("#commentTable tbody");
    if (!tb) return;
    tb.innerHTML = "";
    books.forEach(b => {
        const list = Array.isArray(b.commentTexts) ? b.commentTexts : [];
        const ids  = Array.isArray(b.commentIds) ? b.commentIds : [];
        list.forEach((text, idx) => {
            const row = tb.insertRow();
            row.insertCell().textContent = b.title || '';
            row.insertCell().textContent = text || '';
            const commentId = ids[idx] || '';
            const actions = document.createElement("td");
            const editBtn = document.createElement("button");
            editBtn.textContent = "Edit";
            editBtn.onclick = () => editComment(commentId, b.id);
            const delBtn = document.createElement("button");
            delBtn.textContent = "Delete";
            delBtn.onclick = () => deleteComment(commentId);
            actions.append(editBtn, delBtn);
            row.appendChild(actions);
        });
    });
}

function updateSelects() {
    const bookAuthor = document.getElementById("bookAuthor");
    if (bookAuthor) {
        bookAuthor.innerHTML =
            '<option value="">Select author</option>' +
            authors.map(a => `<option value="${a.id}">${a.fullName}</option>`).join("");
    }
    const bookGenre = document.getElementById("bookGenre");
    if (bookGenre) {
        bookGenre.innerHTML =
            genres.map(g => `<option value="${g.id}">${g.name}</option>`).join("");
    }
    const commentBook = document.getElementById("commentBook");
    if (commentBook) {
        commentBook.innerHTML =
            '<option value="">Select book</option>' +
            books.map(b => `<option value="${b.id}">${b.title}</option>`).join("");
    }
}

document.getElementById("bookForm")?.addEventListener('submit', async e => {
    e.preventDefault();
    const title = document.getElementById("bookTitle").value.trim();
    const authorId = document.getElementById("bookAuthor").value;
    const genreIds = Array.from(document.getElementById("bookGenre").selectedOptions).map(opt => opt.value);

    if (!title || !authorId || genreIds.length === 0) {
        alert("Please fill all book fields");
        return;
    }
    try {
        await fetchJson(`${api}/books`, {
            method: "POST",
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify({ title, authorId, genreIds })
        });
        e.target.reset();
        await refreshAll();
    } catch (err) {
        alert('Failed to create book');
        console.error(err);
    }
});

async function deleteBook(id) {
    if (!confirm("Delete this book?")) return;
    try {
        const res = await fetch(`${api}/books/${id}`, { method: "DELETE" });
        if (!res.ok && res.status !== 204) {
            const t = await res.text().catch(()=> '');
            throw new Error(`HTTP ${res.status}: ${t}`);
        }
        await refreshAll();
    } catch (err) {
        alert('Failed to delete book');
        console.error(err);
    }
}
window.deleteBook = deleteBook;

function openEditBookModal() {
    document.getElementById('editBookModal').style.display = 'flex';
}
function closeEditBookModal() {
    document.getElementById('editBookModal').style.display = 'none';
}
function editBook(id) {
    const b = books.find(x => x.id === id);
    if (!b) return;

    document.getElementById('editBookId').value = b.id;
    document.getElementById('editBookTitle').value = b.title || '';

    const authorSelect = document.getElementById('editBookAuthor');
    authorSelect.innerHTML =
        '<option value="">Select author</option>' +
        authors.map(a => `<option value="${a.id}">${a.fullName}</option>`).join("");
    if (b.authorId) authorSelect.value = b.authorId;

    const genresSelect = document.getElementById('editBookGenres');
    genresSelect.innerHTML = genres.map(g => `<option value="${g.id}">${g.name}</option>`).join("");
    let currentGenreIds = Array.isArray(b.genreIds) ? b.genreIds.slice() : [];
    if ((!currentGenreIds || currentGenreIds.length === 0) && Array.isArray(b.genreNames)) {
        const byName = new Map(genres.map(g => [g.name, g.id]));
        currentGenreIds = b.genreNames.map(n => byName.get(n)).filter(Boolean);
    }
    Array.from(genresSelect.options).forEach(opt => {
        opt.selected = currentGenreIds.includes(opt.value);
    });

    openEditBookModal();
}
window.editBook = editBook;

document.getElementById('editBookForm')?.addEventListener('submit', async (e) => {
    e.preventDefault();
    const id = document.getElementById('editBookId').value;
    const title = document.getElementById('editBookTitle').value.trim();
    const authorId = document.getElementById('editBookAuthor').value;
    const genreIds = Array.from(document.getElementById('editBookGenres').selectedOptions).map(o => o.value);
    if (!id || !title || !authorId || genreIds.length === 0) {
        alert('Please fill all fields');
        return;
    }
    try {
        await fetchJson(`${api}/books/${id}`, {
            method: 'PUT',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify({ id, title, authorId, genreIds })
        });
        closeEditBookModal();
        await refreshAll();
    } catch (err) {
        alert('Failed to save book');
        console.error(err);
    }
});
document.getElementById('cancelEditBookBtn')?.addEventListener('click', () => {
    closeEditBookModal();
});

document.getElementById("authorForm")?.addEventListener('submit', async e => {
    e.preventDefault();
    const fullName = document.getElementById("authorName").value.trim();
    if (!fullName) { alert("Enter author full name"); return; }
    try {
        await fetchJson(`${api}/authors`, {
            method: "POST",
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify({ fullName })
        });
        e.target.reset();
        await refreshAll();
    } catch (err) {
        alert('Failed to create author');
        console.error(err);
    }
});
async function editAuthor(id) {
    const a = authors.find(x => x.id === id);
    if (!a) return;
    const newName = prompt("New full name:", a.fullName);
    if (newName && newName.trim()) {
        try {
            await fetchJson(`${api}/authors/${id}`, {
                method: "PUT",
                headers: {'Content-Type': 'application/json'},
                body: JSON.stringify({ ...a, fullName: newName.trim() })
            });
            await refreshAll();
        } catch (err) {
            alert('Failed to save author');
            console.error(err);
        }
    }
}
window.editAuthor = editAuthor;

async function deleteAuthor(id) {
    if (!confirm("Delete this author?")) return;
    try {
        const res = await fetch(`${api}/authors/${id}`, { method: "DELETE" });
        if (!res.ok && res.status !== 204) {
            const t = await res.text().catch(()=> '');
            throw new Error(`HTTP ${res.status}: ${t}`);
        }
        await refreshAll();
    } catch (err) {
        alert('Failed to delete author');
        console.error(err);
    }
}
window.deleteAuthor = deleteAuthor;

document.getElementById("genreForm")?.addEventListener('submit', async e => {
    e.preventDefault();
    const name = document.getElementById("genreName").value.trim();
    if (!name) { alert("Enter genre name"); return; }
    try {
        await fetchJson(`${api}/genres`, {
            method: "POST",
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify({ name })
        });
        e.target.reset();
        await refreshAll();
    } catch (err) {
        alert('Failed to create genre');
        console.error(err);
    }
});
async function editGenre(id) {
    const g = genres.find(x => x.id === id);
    if (!g) return;
    const newName = prompt("New genre name:", g.name);
    if (newName && newName.trim()) {
        try {
            await fetchJson(`${api}/genres/${id}`, {
                method: "PUT",
                headers: {'Content-Type': 'application/json'},
                body: JSON.stringify({ ...g, name: newName.trim() })
            });
            await refreshAll();
        } catch (err) {
            alert('Failed to save genre');
            console.error(err);
        }
    }
}
window.editGenre = editGenre;

async function deleteGenre(id) {
    if (!confirm("Delete this genre?")) return;
    try {
        const res = await fetch(`${api}/genres/${id}`, { method: "DELETE" });
        if (!res.ok && res.status !== 204) {
            const t = await res.text().catch(()=> '');
            throw new Error(`HTTP ${res.status}: ${t}`);
        }
        await refreshAll();
    } catch (err) {
        alert('Failed to delete genre');
        console.error(err);
    }
}
window.deleteGenre = deleteGenre;

document.getElementById("commentForm")?.addEventListener('submit', async e => {
    e.preventDefault();
    const sel = document.getElementById("commentBook");
    const bookId = sel ? sel.value : '';
    const text   = document.getElementById("commentText").value.trim();

    if (!bookId) {
        alert("Select a book for the comment");
        return;
    }
    if (!text) {
        alert("Enter comment text");
        return;
    }

    try {
        await fetchJson(`${api}/comments`, {
            method: "POST",
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify({ bookId, text })
        });
        document.getElementById("commentText").value = '';
        await refreshAll();
        if (sel) sel.value = bookId;
    } catch (err) {
        alert('Failed to create comment');
        console.error(err);
    }
});

async function editComment(commentId, bookId) {
    if (!commentId) { alert("No comment ID"); return; }
    const newText = prompt("New comment text:");
    if (newText && newText.trim()) {
        try {
            await fetchJson(`${api}/comments/${commentId}`, {
                method: "PUT",
                headers: {'Content-Type': 'application/json'},
                body: JSON.stringify({ id: commentId, bookId, text: newText.trim() })
            });
            await refreshAll();
            const sel = document.getElementById('commentBook');
            if (sel && bookId) sel.value = bookId;
        } catch (err) {
            alert('Failed to save comment');
            console.error(err);
        }
    }
}
window.editComment = editComment;

async function deleteComment(commentId) {
    if (!commentId) { alert("No comment ID"); return; }
    if (!confirm("Delete this comment?")) return;
    try {
        const res = await fetch(`${api}/comments/${commentId}`, { method: "DELETE" });
        if (!res.ok && res.status !== 204) {
            const t = await res.text().catch(()=> '');
            throw new Error(`HTTP ${res.status}: ${t}`);
        }
        await refreshAll();
    } catch (err) {
        alert('Failed to delete comment');
        console.error(err);
    }
}
window.deleteComment = deleteComment;

document.addEventListener('DOMContentLoaded', refreshAll);

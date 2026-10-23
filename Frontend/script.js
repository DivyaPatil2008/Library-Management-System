/* =========================================
   LIBRARY MANAGEMENT SYSTEM
   MAIN JAVASCRIPT
   ========================================= */

// ================= USER DATA =================

const loggedInUser =
    localStorage.getItem("loggedInUser");

const userRole =
    localStorage.getItem("userRole");

const isAdmin =
    userRole &&
    userRole.toUpperCase() === "ADMIN";


// ================= PAGE ELEMENTS =================

const searchInput =
    document.getElementById("searchInput");

const searchButton =
    document.querySelector(".search-button");

const resetButton =
    document.querySelector(".reset-button");


// ================= USER PROFILE =================

const userNameElement =
    document.getElementById("userName");

const userRoleElement =
    document.getElementById("userRole");

const userAvatar =
    document.getElementById("userAvatar");

const welcomeName =
    document.getElementById("welcomeName");

const welcomeMessage =
    document.getElementById("welcomeMessage");


if (loggedInUser) {

    const displayName =
        loggedInUser.charAt(0).toUpperCase() +
        loggedInUser.slice(1);

    if (userNameElement) {
        userNameElement.textContent =
            displayName;
    }

    if (userAvatar) {
        userAvatar.textContent =
            loggedInUser.charAt(0).toUpperCase();
    }

    if (welcomeName) {
        welcomeName.textContent =
            displayName;
    }
}


if (userRoleElement) {

    if (isAdmin) {

        userRoleElement.textContent =
            "Library Administrator";

    } else {

        userRoleElement.textContent =
            "Library User";
    }
}


if (welcomeMessage) {

    if (isAdmin) {

        welcomeMessage.textContent =
            "Keep your library organized and manage books, issues and returns from one place.";

    } else {

        welcomeMessage.textContent =
            "Explore books, search the library, and manage your issued books from one place.";
    }
}


// ================= ADMIN PERMISSIONS =================

function applyPermissions() {

    const menuItems =
        document.querySelectorAll(".menu-item");

    menuItems.forEach(function(item) {

        const text =
            item.textContent.trim();

        if (!isAdmin) {

            if (
                text.includes("Add Book") ||
                text.includes("Update Book") ||
                text.includes("Delete Book")
            ) {

                item.style.display = "none";
            }
        }
    });


    const addButton =
        document.querySelector(".add-button");

    if (addButton && !isAdmin) {

        addButton.style.display = "none";
    }
}

applyPermissions();


// ================= DASHBOARD =================

function dashboard() {

    viewBooks();
    loadStats();
}


// ================= VIEW BOOKS =================
async function viewBooks() {
    try {
        const response = await fetch("http://localhost:8080/books");

        const books = await response.json();

        console.log("Books received:", books);

        displayBooks(books);
        updateShowingCount(books.length);
        loadStats();

    } catch (error) {
        console.error("View Books error:", error);
        alert("Unable to load books. Check Console.");
    }
}



// ================= PAGINATION =================

let currentBookPage = 1;
const booksPerPage = 5;
let allDisplayedBooks = [];

// ================= DISPLAY BOOKS =================

function displayBooks(books) {
    allDisplayedBooks = books;

    const totalPages = Math.max(
        1,
        Math.ceil(allDisplayedBooks.length / booksPerPage)
    );

    if (currentBookPage > totalPages) {
        currentBookPage = totalPages;
    }

    const table = document.getElementById("bookTable");
    if (!table) return;

    table.innerHTML = "";

    const startIndex = (currentBookPage - 1) * booksPerPage;
    const pageBooks = allDisplayedBooks.slice(
        startIndex,
        startIndex + booksPerPage
    );

    pageBooks.forEach(function(book) {
        const row = document.createElement("tr");

        const status = Number(book.available) === 1
            ? "Available"
            : "Issued";

        const statusClass = Number(book.available) === 1
            ? "available"
            : "issued";

        row.innerHTML = `
            <td>${book.book_id}</td>
            <td><strong>${escapeHtml(book.title)}</strong></td>
            <td>${escapeHtml(book.author)}</td>
            <td>
                <span class="status ${statusClass}">
                    ${status}
                </span>
            </td>
            <td>
                <button class="action-button"
                    onclick="bookActions(${book.book_id}, ${Number(book.available)})">
                    ⋮
                </button>
            </td>
        `;

        table.appendChild(row);
    });

    renderBookPagination(totalPages);
}

// ================= PAGE BUTTONS =================

function renderBookPagination(totalPages) {
    const container = document.getElementById("pagination");
    if (!container) return;

    container.innerHTML = "";

    function addButton(label, page, disabled = false) {
        const button = document.createElement("button");
        button.textContent = label;
        button.disabled = disabled;

        if (page === currentBookPage) {
            button.classList.add("page-active");
        }

        button.addEventListener("click", function() {
            if (page < 1 || page > totalPages) return;

            currentBookPage = page;
            displayBooks(allDisplayedBooks);
        });

        container.appendChild(button);
    }

    addButton("‹", currentBookPage - 1, currentBookPage === 1);

    for (let page = 1; page <= totalPages; page++) {
        addButton(String(page), page);
    }

    addButton("›", currentBookPage + 1,
        currentBookPage === totalPages);
}



/* ===== CLICKABLE DASHBOARD CARDS ===== */

let selectedBookCard = null;

async function toggleBookDetails(type) {
    const panel = document.getElementById("bookDetailsPanel");
    const title = document.getElementById("bookDetailsTitle");
    const content = document.getElementById("bookDetailsContent");

    if (!panel || !title || !content) {
        console.error("Book details panel not found in index.html");
        return;
    }

    // Same card clicked again: hide details
    if (selectedBookCard === type && panel.style.display !== "none") {
        panel.style.display = "none";
        selectedBookCard = null;
        return;
    }

    selectedBookCard = type;
    panel.style.display = "block";
    title.textContent = "Loading books...";
    content.innerHTML = "";

    try {
        const response = await fetch("http://localhost:8080/books");

        if (!response.ok) {
            throw new Error("Unable to load books");
        }

        const books = await response.json();
        console.log("Books received:", books);
console.log("Book table:", document.getElementById("bookTable"));

displayBooks(books);

        let filteredBooks = books;

        if (type === "available") {
            filteredBooks = books.filter(book => Number(book.available) === 1);
            title.textContent = "Available Books";
        } else if (type === "issued") {
            filteredBooks = books.filter(book => Number(book.available) === 0);
            title.textContent = "Issued Books";
        } else {
            title.textContent = "All Books";
        }

        if (filteredBooks.length === 0) {
            content.textContent = "No books found.";
            return;
        }

        const table = document.createElement("table");
        table.className = "books-table";

        table.innerHTML = `
            <thead>
                <tr>
                    <th>Book ID</th>
                    <th>Title</th>
                    <th>Author</th>
                    <th>Status</th>
                </tr>
            </thead>
            <tbody></tbody>
        `;

        const tbody = table.querySelector("tbody");

        filteredBooks.forEach(book => {
            const row = document.createElement("tr");
            const status = Number(book.available) === 1
                ? "Available"
                : "Issued";

            [book.book_id, book.title, book.author, status]
                .forEach(value => {
                    const cell = document.createElement("td");
                    cell.textContent = value ?? "";
                    row.appendChild(cell);
                });

            tbody.appendChild(row);
        });

        content.replaceChildren(table);

        // Scroll to the details panel
        panel.scrollIntoView({
            behavior: "smooth",
            block: "nearest"
        });

    } catch (error) {
        console.error(error);
        title.textContent = "Unable to load book details";
        content.textContent = "Please check that the Java server is running.";
        selectedBookCard = null;
    }
}




// ================= BOOK ACTIONS =================

function bookActions(bookId, available) {

    if (isAdmin) {

        const choice =
            prompt(
                "Choose action:\n\n" +
                "1 - Update Book\n" +
                "2 - Delete Book"
            );

        if (choice === "1") {

            updateBookById(bookId);

        } else if (choice === "2") {

            deleteBookById(bookId);
        }

    } else {

        if (available == 1) {

            issueBookById(bookId);

        } else {

            returnBookById(bookId);
        }
    }
}


// ================= SEARCH BAR =================

if (searchButton) {

    searchButton.addEventListener(
        "click",
        searchBooks
    );
}


if (searchInput) {

    searchInput.addEventListener(
        "keyup",
        function(event) {

            if (event.key === "Enter") {
                searchBooks();
            }
        }
    );
}


async function searchBooks() {

    const searchText =
        searchInput.value
            .toLowerCase()
            .trim();

    try {

        const response =
            await fetch(
                "http://localhost:8080/books"
            );

        const books =
            await response.json();

        const results =
            books.filter(function(book) {

                return (
                    String(book.book_id)
                        .includes(searchText)

                    ||

                    book.title
                        .toLowerCase()
                        .includes(searchText)

                    ||

                    book.author
                        .toLowerCase()
                        .includes(searchText)
                );
            });

        displayBooks(results);

        updateShowingCount(
            results.length
        );

    } catch (error) {

        console.error(error);

        alert("Unable to search books.");
    }
}


// ================= RESET SEARCH =================

if (resetButton) {

    resetButton.addEventListener(
        "click",
        function() {

            searchInput.value = "";

            viewBooks();
        }
    );
}


// ================= SIDEBAR SEARCH =================

async function searchBook() {

    const keyword =
        prompt(
            "Enter Book ID, title or author:"
        );

    if (!keyword) return;

    if (searchInput) {
        searchInput.value = keyword;
    }

    await searchBooks();
}


// ================= ADD BOOK =================

async function addBook() {

    if (!isAdmin) {

        alert("Only Admin can add books.");

        return;
    }

    const title =
        prompt("Enter book title:");

    if (!title) return;

    const author =
        prompt("Enter author name:");

    if (!author) return;

    const bookId =
        prompt("Enter book ID:");

    if (!bookId) return;

    try {

        const response =
            await fetch(
                "http://localhost:8080/add-book",
                {
                    method: "POST",

                    headers: {
                        "Content-Type":
                            "application/json"
                    },

                    body: JSON.stringify({

                        username:
                            loggedInUser,

                        book_id:
                            bookId,

                        title:
                            title,

                        author:
                            author
                    })
                }
            );

        const result =
            await response.text();

        alert(result);

        if (
            result ===
            "Book added successfully!"
        ) {

            viewBooks();
        }

    } catch (error) {

        console.error(error);

        alert("Unable to add book.");
    }
}


// ================= ISSUE BOOK =================

async function issueBook() {

    if (!loggedInUser) {

        alert("Please login first.");

        return;
    }

    const bookId =
        prompt("Enter Book ID to issue:");

    if (!bookId) return;

    await issueBookById(bookId);
}


async function issueBookById(bookId) {

    try {

        const response =
            await fetch(
                "http://localhost:8080/issue-book",
                {
                    method: "POST",

                    headers: {
                        "Content-Type":
                            "application/json"
                    },

                    body: JSON.stringify({

                        username:
                            loggedInUser,

                        book_id:
                            bookId
                    })
                }
            );

        const result =
            await response.text();

        alert(result);

        if (
            result ===
            "Book issued successfully!"
        ) {

            viewBooks();
        }

    } catch (error) {

        console.error(error);

        alert("Unable to issue book.");
    }
}


// ================= RETURN BOOK =================

async function returnBook() {

    if (!loggedInUser) {

        alert("Please login first.");

        return;
    }

    const bookId =
        prompt("Enter Book ID to return:");

    if (!bookId) return;

    await returnBookById(bookId);
}


async function returnBookById(bookId) {

    try {

        const response =
            await fetch(
                "http://localhost:8080/return-book",
                {
                    method: "POST",

                    headers: {
                        "Content-Type":
                            "application/json"
                    },

                    body: JSON.stringify({

                        username:
                            loggedInUser,

                        book_id:
                            bookId
                    })
                }
            );

        const result =
            await response.text();

        alert(result);

        if (
            result ===
            "Book returned successfully!"
        ) {

            viewBooks();
        }

    } catch (error) {

        console.error(error);

        alert("Unable to return book.");
    }
}


// ================= UPDATE BOOK =================

async function updateBook() {

    if (!isAdmin) {

        alert("Only Admin can update books.");

        return;
    }

    const bookId =
        prompt("Enter Book ID to update:");

    if (!bookId) return;

    updateBookById(bookId);
}


async function updateBookById(bookId) {

    if (!isAdmin) {

        alert("Only Admin can update books.");

        return;
    }

    const title =
        prompt("Enter new book title:");

    if (!title) return;

    const author =
        prompt("Enter new author name:");

    if (!author) return;

    try {

        const response =
            await fetch(
                "http://localhost:8080/update-book",
                {
                    method: "POST",

                    headers: {
                        "Content-Type":
                            "application/json"
                    },

                    body: JSON.stringify({

                        username:
                            loggedInUser,

                        book_id:
                            bookId,

                        title:
                            title,

                        author:
                            author
                    })
                }
            );

        const result =
            await response.text();

        alert(result);

        if (
            result ===
            "Book updated successfully!"
        ) {

            viewBooks();
        }

    } catch (error) {

        console.error(error);

        alert("Unable to update book.");
    }
}


// ================= DELETE BOOK =================

async function deleteBook() {

    if (!isAdmin) {

        alert("Only Admin can delete books.");

        return;
    }

    const bookId =
        prompt("Enter Book ID to delete:");

    if (!bookId) return;

    deleteBookById(bookId);
}


async function deleteBookById(bookId) {

    if (!isAdmin) {

        alert("Only Admin can delete books.");

        return;
    }

    const confirmDelete =
        confirm(
            "Are you sure you want to delete Book ID " +
            bookId +
            "?"
        );

    if (!confirmDelete) return;

    try {

        const response =
            await fetch(
                "http://localhost:8080/delete-book",
                {
                    method: "POST",

                    headers: {
                        "Content-Type":
                            "application/json"
                    },

                    body: JSON.stringify({

                        username:
                            loggedInUser,

                        book_id:
                            bookId
                    })
                }
            );

        const result =
            await response.text();

        alert(result);

        if (
            result ===
            "Book deleted successfully!"
        ) {

            viewBooks();
        }

    } catch (error) {

        console.error(error);

        alert("Unable to delete book.");
    }
}


// ================= DASHBOARD STATS =================

async function loadStats() {

    try {

        const response =
            await fetch(
                "http://localhost:8080/stats"
            );

        const stats =
            await response.json();

        const totalBooks =
            document.getElementById(
                "totalBooks"
            );

        const availableBooks =
            document.getElementById(
                "availableBooks"
            );

        const issuedBooks =
            document.getElementById(
                "issuedBooks"
            );

        if (totalBooks) {
            totalBooks.textContent =
                stats.total;
        }

        if (availableBooks) {
            availableBooks.textContent =
                stats.available;
        }

        if (issuedBooks) {
            issuedBooks.textContent =
                stats.issued;
        }

    } catch (error) {

        console.error(
            "Unable to load statistics."
        );
    }
}


// ================= SHOWING COUNT =================

function updateShowingCount(count) {

    const showing =
        document.getElementById(
            "showingBooks"
        );

    if (showing) {
        showing.textContent = count;
    }
}


// ================= LOGOUT =================

function logout() {

    const confirmLogout =
        confirm(
            "Are you sure you want to logout?"
        );

    if (!confirmLogout) {
        return;
    }

    localStorage.removeItem(
        "loggedInUser"
    );

    localStorage.removeItem(
        "userRole"
    );

    window.location.href =
        "login.html";
}


// ================= ESCAPE HTML =================

function escapeHtml(text) {

    const div =
        document.createElement("div");

    div.textContent = text;

    return div.innerHTML;
}


// ================= SIDEBAR ACTIVE =================

const menuItems =
    document.querySelectorAll(
        ".menu-item"
    );

menuItems.forEach(function(item) {

    item.addEventListener(
        "click",
        function() {

            menuItems.forEach(
                function(menu) {

                    menu.classList.remove(
                        "active"
                    );
                }
            );

            item.classList.add(
                "active"
            );
        }
    );
});


// ================= LOGIN =================

const loginForm =
    document.getElementById("loginForm");

if (loginForm) {

    loginForm.addEventListener(
        "submit",
        async function(event) {

            event.preventDefault();

            const username =
                document
                    .getElementById("username")
                    .value
                    .trim();

            const password =
                document
                    .getElementById("password")
                    .value
                    .trim();

            const role =
                document
                    .getElementById("role")
                    .value;

            if (
                !username ||
                !password ||
                !role
            ) {

                alert(
                    "Please fill all fields."
                );

                return;
            }

            try {

                const response =
                    await fetch(
                        "http://localhost:8080/login",
                        {
                            method: "POST",

                            headers: {
                                "Content-Type":
                                    "application/json"
                            },

                            body:
                                JSON.stringify({

                                    username:
                                        username,

                                    password:
                                        password,

                                    role:
                                        role
                                })
                        }
                    );

                const result =
                    await response.text();

                if (response.ok) {

                    localStorage.setItem(
                        "loggedInUser",
                        username
                    );

                    localStorage.setItem(
                        "userRole",
                        role
                    );

                    window.location.href =
                        "index.html";

                } else {

                    alert(result);
                }

            } catch (error) {

                console.error(error);

                alert(
                    "Unable to connect to login server."
                );
            }
        }
    );
}


// ================= CREATE ACCOUNT =================

const createAccountForm =
    document.getElementById(
        "createAccountForm"
    );

if (createAccountForm) {

    createAccountForm.addEventListener(
        "submit",
        async function(event) {

            event.preventDefault();

            const username =
                document
                    .getElementById(
                        "accountName"
                    )
                    .value
                    .trim();

            const password =
                document.getElementById(
                    "accountPassword"
                ).value;

            const confirmPassword =
                document.getElementById(
                    "confirmAccountPassword"
                ).value;

            const role =
                document.getElementById(
                    "accountRole"
                ).value;

            const message =
                document.getElementById(
                    "createAccountMessage"
                );

            if (
                !username ||
                !password ||
                !confirmPassword ||
                !role
            ) {

                if (message) {

                    message.textContent =
                        "Please fill all fields.";

                    message.style.color =
                        "red";
                }

                return;
            }

            if (password !== confirmPassword) {

                if (message) {

                    message.textContent =
                        "Passwords do not match.";

                    message.style.color =
                        "red";
                }

                return;
            }

            try {

                const response =
                    await fetch(
                        "http://localhost:8080/create-account",
                        {
                            method: "POST",

                            headers: {
                                "Content-Type":
                                    "application/json"
                            },

                            body:
                                JSON.stringify({

                                    username:
                                        username,

                                    password:
                                        password,

                                    role:
                                        role
                                })
                        }
                    );

                const result =
                    await response.text();

                if (response.ok) {

                    if (message) {

                        message.textContent =
                            "Account created successfully!";

                        message.style.color =
                            "green";
                    }

                    setTimeout(
                        function() {

                            window.location.href =
                                "login.html";

                        },
                        1200
                    );

                } else {

                    if (message) {

                        message.textContent =
                            result;

                        message.style.color =
                            "red";
                    }
                }

            } catch (error) {

                console.error(error);

                if (message) {

                    message.textContent =
                        "Unable to connect to server.";

                    message.style.color =
                        "red";
                }
            }
        }
    );
}


// ================= SHOW / HIDE PASSWORD =================

function setupPasswordToggle(
    buttonId,
    inputId
) {

    const button =
        document.getElementById(
            buttonId
        );

    const input =
        document.getElementById(
            inputId
        );

    if (button && input) {

        button.addEventListener(
            "click",
            function() {

                if (
                    input.type ===
                    "password"
                ) {

                    input.type =
                        "text";

                    button.textContent =
                        "Hide";

                } else {

                    input.type =
                        "password";

                    button.textContent =
                        "Show";
                }
            }
        );
    }
}


setupPasswordToggle(
    "showPassword",
    "password"
);

setupPasswordToggle(
    "showAccountPassword",
    "accountPassword"
);

setupPasswordToggle(
    "showConfirmAccountPassword",
    "confirmAccountPassword"
);

setupPasswordToggle(
    "showNewPassword",
    "newPassword"
);

setupPasswordToggle(
    "showConfirmPassword",
    "confirmPassword"
);


// ================= FORGOT PASSWORD =================

const forgotPassword =
    document.getElementById(
        "forgotPassword"
    );

if (forgotPassword) {

    forgotPassword.addEventListener(
        "click",
        function() {

            window.location.href =
                "reset-password.html";
        }
    );
}


// ================= PAGE LOAD =================

if (loggedInUser) {

    viewBooks();
    loadStats();
}


console.log(
    "Library Management System loaded successfully."
);

const resetPasswordForm = document.getElementById("resetPasswordForm");

if (resetPasswordForm) {
    resetPasswordForm.addEventListener("submit", async function(event) {

        event.preventDefault();

        const username = document.getElementById("resetUsername").value.trim();
        const newPassword = document.getElementById("newPassword").value.trim();
        const confirmPassword = document.getElementById("confirmPassword").value.trim();
        const message = document.getElementById("resetMessage");

        if (newPassword !== confirmPassword) {
            message.textContent = "Passwords do not match.";
            message.style.color = "red";
            return;
        }

        try {

            const response = await fetch("http://localhost:8080/reset-password", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({
                    username: username,
                    newPassword: newPassword,
                    confirmPassword: confirmPassword
                })
            });

            const result = await response.text();

            if (response.ok) {

                message.textContent = result;
                message.style.color = "green";

                setTimeout(function() {
                    window.location.href = "login.html";
                }, 1200);

            } else {

                message.textContent = result;
                message.style.color = "red";
            }

        } catch (error) {

            console.error(error);

            message.textContent = "Unable to connect to reset password server.";
            message.style.color = "red";
        }
    });
}

// ================= DARK MODE =================

const darkModeButton = document.getElementById("darkModeButton");

if (darkModeButton) {

    darkModeButton.addEventListener("click", function () {

        document.body.classList.toggle("dark-mode");

        if (document.body.classList.contains("dark-mode")) {
            darkModeButton.innerHTML = "☀️ Light Mode";
        } else {
            darkModeButton.innerHTML = "🌙 Dark Mode";
        }

    });

}



// ================= USER-WISE ISSUED BOOKS =================


let allUserIssuedBooks = [];

function setupIssuedBooksAccess() {
    const section = document.getElementById("userIssuedBooksSection");

    if (!section) return;

    section.style.display = loggedInUser ? "block" : "none";

    const heading = section.querySelector("h2");

    if (heading) {
        heading.textContent = isAdmin
            ? "Users & Their Issued Books"
            : "My Issued Books";
    }

    if (loggedInUser) {
        loadUserIssuedBooks();
    }
}

async function loadUserIssuedBooks() {
    const table = document.getElementById("userIssuedBooksTable");

    if (!table) {
        console.error("Missing element: userIssuedBooksTable");
        return;
    }

    table.innerHTML =
        '<tr><td colspan="8">Loading records...</td></tr>';

    try {
        const response = await fetch(
            "/user-issued-books",
            {
                method: "GET",
                credentials: "same-origin",
                cache: "no-store"
            }
        );

        const result = await response.json();

        if (!response.ok) {
            throw new Error(
                result.error || "Unable to load issued books."
            );
        }

        if (!Array.isArray(result)) {
            throw new Error("Invalid data received from server.");
        }

        console.log("Issued-books API response:", result);

        allUserIssuedBooks = result;
        displayUserIssuedBooks(allUserIssuedBooks);

    } catch (error) {
        console.error("Load issued books error:", error);

        table.innerHTML = "";

        const row = document.createElement("tr");
        const cell = document.createElement("td");

        cell.colSpan = 8;
        cell.textContent = error.message;

        row.appendChild(cell);
        table.appendChild(row);
    }
}

function displayUserIssuedBooks(records) {
    const table = document.getElementById("userIssuedBooksTable");

    if (!table) return;

    table.innerHTML = "";

    if (!Array.isArray(records) || records.length === 0) {
        const row = document.createElement("tr");
        const cell = document.createElement("td");

        cell.colSpan = 8;
        cell.textContent = isAdmin
            ? "No users found."
            : "No issued books found.";

        row.appendChild(cell);
        table.appendChild(row);
        return;
    }

    const users = new Map();

    records.forEach(function(record) {
        const userId = record.user_id;

        if (!users.has(userId)) {
            users.set(userId, {
                user_id: userId,
                name: record.name || "",
                books: []
            });
        }

        if (record.book_id !== null &&
            record.book_id !== undefined) {
            users.get(userId).books.push(record);
        }
    });

    users.forEach(function(user) {
        const count = user.books.length;

        if (count === 0) {
            addIssuedBookRow(table, [
                user.user_id,
                user.name,
                0,
                "—",
                "No books currently issued",
                "—",
                "—",
                "—"
            ]);
            return;
        }

        user.books.forEach(function(book, index) {
            addIssuedBookRow(table, [
                user.user_id,
                user.name,
                index === 0 ? count : "",
                book.book_id,
                book.title || "",
                book.author || "",
                book.issue_date || "—",
                book.due_date || "—"
            ]);
        });
    });
}

function addIssuedBookRow(table, values) {
    const row = document.createElement("tr");

    values.forEach(function(value) {
        const cell = document.createElement("td");

        cell.textContent =
            value === null || value === undefined || value === ""
                ? "—"
                : String(value);

        row.appendChild(cell);
    });

    table.appendChild(row);
}

function filterUserIssuedBooks() {
    const input = document.getElementById("userIssuedSearch");

    if (!input) return;

    const query = input.value.trim().toLowerCase();

    const filteredRecords = allUserIssuedBooks.filter(function(record) {
        return [
            record.user_id,
            record.name,
            record.book_id,
            record.title,
            record.author,
            record.issue_date,
            record.due_date
        ].some(function(value) {
            return String(value ?? "")
                .toLowerCase()
                .includes(query);
        });
    });

    displayUserIssuedBooks(filteredRecords);
}

setupIssuedBooksAccess();


// ================= DISPLAY USER RECORDS =================

function displayUserIssuedBooks(records) {
    const table = document.getElementById(
        "userIssuedBooksTable"
    );

    if (!table) return;

    table.innerHTML = "";

    if (!records || records.length === 0) {
        table.innerHTML = `
            <tr>
                <td colspan="8">No users found.</td>
            </tr>
        `;
        return;
    }

    // Group by user ID so users with the same name remain separate.
    const users = new Map();

    records.forEach(function (record) {
        const userId = record.user_id;

        if (!users.has(userId)) {
            users.set(userId, {
                user_id: userId,
                name: record.name,
                books: []
            });
        }

        if (record.book_id !== null && record.book_id !== undefined) {
            users.get(userId).books.push(record);
        }
    });

    users.forEach(function (user) {
        const count = user.books.length;

        // Show a separate row for users who have no active books.
        if (count === 0) {
            const row = document.createElement("tr");

            row.innerHTML = `
                <td>${user.user_id}</td>
                <td>${escapeHtml(user.name || "")}</td>
                <td>0</td>
                <td colspan="5">No books currently issued</td>
            `;

            table.appendChild(row);
            return;
        }

        user.books.forEach(function (book, index) {
            const row = document.createElement("tr");

            row.innerHTML = `
                <td>${user.user_id}</td>

                <td>${escapeHtml(user.name || "")}</td>

                <td>${index === 0 ? count : ""}</td>

                <td>${book.book_id}</td>

                <td>${escapeHtml(book.title || "")}</td>

                <td>${escapeHtml(book.author || "")}</td>

                <td>${escapeHtml(book.issue_date || "—")}</td>

                <td>${escapeHtml(book.due_date || "—")}</td>
            `;

            table.appendChild(row);
        });
    });
}


// ================= SEARCH USER RECORDS =================

function filterUserIssuedBooks() {
    const input = document.getElementById("userIssuedSearch");

    if (!input) return;

    const query = input.value.trim().toLowerCase();

    const filtered = allUserIssuedBooks.filter(function (record) {
        return [
            record.user_id,
            record.name,
            record.book_id,
            record.title,
            record.author,
            record.issue_date,
            record.due_date
        ].some(function (value) {
            return String(value ?? "")
                .toLowerCase()
                .includes(query);
        });
    });

    displayUserIssuedBooks(filtered);
}

setupIssuedBooksAccess();
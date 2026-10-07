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

        const response =
            await fetch(
                "http://localhost:8080/books"
            );

        if (!response.ok) {
            throw new Error("Unable to load books");
        }

        const books =
            await response.json();

        displayBooks(books);

        updateShowingCount(books.length);

        loadStats();

    } catch (error) {

        console.error(error);

        alert("Unable to load books.");
    }
}


// ================= DISPLAY BOOKS =================

function displayBooks(books) {

    const table =
        document.getElementById("bookTable");

    if (!table) return;

    table.innerHTML = "";

    books.forEach(function(book) {

        const row =
            document.createElement("tr");

        const status =
            book.available == 1
                ? "Available"
                : "Issued";

        const statusClass =
            book.available == 1
                ? "available"
                : "issued";

        row.innerHTML = `
            <td>
                ${book.book_id}
            </td>

            <td>
                <strong>
                    ${escapeHtml(book.title)}
                </strong>
            </td>

            <td>
                ${escapeHtml(book.author)}
            </td>

            <td>
                <span class="status ${statusClass}">
                    ${status}
                </span>
            </td>

            <td>
                <button
                    class="action-button"
                    onclick="bookActions(${book.book_id}, ${book.available})">
                    ⋮
                </button>
            </td>
        `;

        table.appendChild(row);
    });
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
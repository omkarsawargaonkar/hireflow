/*
 * HireFlow session helper.
 * The logged-in user is kept in one place: currentUser.
 */

function getCurrentUser() {
    try {
        return JSON.parse(sessionStorage.getItem("currentUser"));
    } catch (error) {
        return null;
    }
}


function getAuthToken() {
    return sessionStorage.getItem("authToken");
}

async function authFetch(url, options = {}) {
    const headers = new Headers(options.headers || {});
    const token = getAuthToken();

    if (token) {
        headers.set("Authorization", `Bearer ${token}`);
    }

    const response = await fetch(url, { ...options, headers });

    if (response.status === 401) {
        sessionStorage.removeItem("currentUser");
        sessionStorage.removeItem("authToken");
        if (!window.location.pathname.endsWith("login.html")) {
            window.location.href = "login.html";
        }
    }

    return response;
}

function requireRole(role) {
    const user = getCurrentUser();

    if (!user) {
        window.location.href = "login.html";
        return null;
    }

    if (user.role !== role) {
        if (user.role === "RECRUITER") {
            window.location.href = "recruiter-dashboard.html";
        } else if (user.role === "CANDIDATE") {
            window.location.href = "jobs.html";
        } else {
            window.location.href = "index.html";
        }
        return null;
    }

    return user;
}

function logout() {
    sessionStorage.removeItem("currentUser");
    sessionStorage.removeItem("authToken");
    sessionStorage.removeItem("candidateId");
    sessionStorage.removeItem("recruiterId");
    localStorage.removeItem("currentUser");
    localStorage.removeItem("authToken");
    localStorage.removeItem("candidateId");
    localStorage.removeItem("recruiterId");
    window.location.href = "index.html";
}

function renderCurrentUser() {
    const user = getCurrentUser();
    const nav = document.querySelector(".nav-links");

    if (!nav) return;

    // Build navigation according to the logged-in role so candidate and
    // recruiter actions are clearly separated.
    const page = window.location.pathname.split("/").pop();
    if (user?.role === "RECRUITER") {
        nav.querySelectorAll('a[href="applications.html"], a[href="login.html"], a[href="jobs.html"]').forEach(link => link.remove());
        if (!nav.querySelector('a[href="recruiter-dashboard.html"]')) {
            const dashboardLink = document.createElement("a");
            dashboardLink.href = "recruiter-dashboard.html";
            dashboardLink.textContent = "Recruiter Dashboard";
            nav.appendChild(dashboardLink);
        }
    } else if (user?.role === "CANDIDATE") {
        nav.querySelectorAll('a[href="recruiter-dashboard.html"], a[data-recruiter-applications-link="true"]').forEach(link => link.remove());
        if (!nav.querySelector('a[href="applications.html"]')) {
            const applicationsLink = document.createElement("a");
            applicationsLink.href = "applications.html";
            applicationsLink.textContent = "Applications";
            nav.appendChild(applicationsLink);
        }
    }

    const oldSession = document.getElementById("hireflow-session-menu");
    if (oldSession) oldSession.remove();

    const sessionMenu = document.createElement("span");
    sessionMenu.id = "hireflow-session-menu";
    sessionMenu.style.display = "inline-flex";
    sessionMenu.style.alignItems = "center";
    sessionMenu.style.gap = "10px";
    sessionMenu.style.marginLeft = "10px";

    if (user) {
        sessionMenu.innerHTML = `
            <span style="font-weight:600; color:#1e293b;">${escapeHtml(user.name)}</span>
            <span style="font-size:12px; color:#64748b;">${user.role === "RECRUITER" ? "Recruiter" : "Candidate"}</span>
            <button type="button" onclick="logout()" style="border:0; background:none; color:#2563eb; cursor:pointer; font-weight:600;">Logout</button>
        `;
    } else {
        sessionMenu.innerHTML = `<a href="login.html">Login</a>`;
    }

    nav.appendChild(sessionMenu);
}

function escapeHtml(value) {
    return String(value ?? "")
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll('"', "&quot;")
        .replaceAll("'", "&#039;");
}

document.addEventListener("DOMContentLoaded", () => {
    const page = window.location.pathname.split("/").pop();

    // The public job-search page is for candidates. Recruiters should land
    // on their dashboard even if they open jobs.html directly.
    if (page === "jobs.html" && getCurrentUser()?.role === "RECRUITER") {
        window.location.replace("recruiter-dashboard.html");
        return;
    }

    if (page === "applications.html") {
        if (!requireRole("CANDIDATE")) return;
    }

    if (page === "recruiter-dashboard.html" || page === "recruiter-applications.html") {
        if (!requireRole("RECRUITER")) return;
    }

    renderCurrentUser();
});

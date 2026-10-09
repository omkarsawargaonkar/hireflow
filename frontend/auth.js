const API_BASE_URL = "http://localhost:8080/api";

const loginForm = document.getElementById("login-form");

if (loginForm) {
    loginForm.addEventListener("submit", loginUser);
}

async function loginUser(event) {
    event.preventDefault();

    const email = document.getElementById("login-email").value.trim();
    const password = document.getElementById("login-password").value;
    const message = document.getElementById("login-message");

    message.textContent = "Signing you in...";
    message.style.color = "#64748b";

    try {
        const response = await fetch(`${API_BASE_URL}/auth/login`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({ email, password })
        });

        const data = await response.json();

        if (!response.ok) {
            message.textContent = data.message || "Invalid email or password.";
            message.style.color = "#dc2626";
            return;
        }

        // Keep authentication data isolated to this browser tab.
        // localStorage is shared across tabs and caused candidate/recruiter sessions to overwrite each other.
        sessionStorage.setItem("currentUser", JSON.stringify(data.user));
        sessionStorage.setItem("authToken", data.token);

        // Remove legacy shared values so older code cannot reuse a stale session.
        localStorage.removeItem("currentUser");
        localStorage.removeItem("authToken");
        localStorage.removeItem("candidateId");
        localStorage.removeItem("recruiterId");

        message.textContent = `Welcome back, ${data.user.name}!`;
        message.style.color = "#16a34a";

        setTimeout(() => {
            if (data.user.role === "RECRUITER") {
                window.location.href = "recruiter-dashboard.html";
            } else if (data.user.role === "CANDIDATE") {
                window.location.href = "jobs.html";
            } else {
                window.location.href = "index.html";
            }
        }, 500);

    } catch (error) {
        console.error("Login error:", error);
        message.textContent = "Unable to connect to the server.";
        message.style.color = "#dc2626";
    }
}

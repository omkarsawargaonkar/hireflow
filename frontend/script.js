const jobsContainer = document.getElementById("jobs-container");
const loggedInUser = typeof getCurrentUser === "function" ? getCurrentUser() : null;
const recruiterOnJobsPage =
    window.location.pathname.split("/").pop() === "jobs.html" &&
    loggedInUser?.role === "RECRUITER";

if (recruiterOnJobsPage) {
    window.location.replace("recruiter-dashboard.html");
} else if (jobsContainer) {
    loadJobs();
}

async function loadJobs() {

    try {

        const response = await authFetch("http://localhost:8080/api/jobs");

        if (!response.ok) {
            throw new Error("Failed to load jobs");
        }

        const jobs = await response.json();

        displayJobs(jobs);

    } catch (error) {

        jobsContainer.innerHTML = `
            <p>Unable to load jobs. Please try again later.</p>
        `;

        console.error(error);
    }
}


function displayJobs(jobs) {

    jobsContainer.innerHTML = "";

    if (jobs.length === 0) {

        jobsContainer.innerHTML = `
            <p>No jobs are currently available.</p>
        `;

        return;
    }

    jobs.forEach(job => {

        const jobCard = document.createElement("div");

        jobCard.classList.add("job-card");

        jobCard.innerHTML = `
            <h2>${job.title}</h2>

            <p class="job-company">
                ${job.companyName}
            </p>

            <p class="job-location">
                ${job.location} · ${job.employmentType}
            </p>

            <p class="job-description">
                ${job.description}
            </p>

            <p class="job-skills">
                <strong>Skills:</strong> ${job.skills}
            </p>

            <div class="job-bottom">

                <span class="job-salary">
                    ${job.salary}
                </span>

                <a href="job-details.html?id=${job.id}" class="view-job-btn">
    View Job
</a>

            </div>
        `;

        jobsContainer.appendChild(jobCard);
    });
}

const jobDetailsContainer =
    document.getElementById("job-details-container");

if (jobDetailsContainer) {
    loadJobDetails();
}

async function loadJobDetails() {

    const urlParams = new URLSearchParams(window.location.search);

    const jobId = urlParams.get("id");

    if (!jobId) {
        jobDetailsContainer.innerHTML = `
            <p>Job ID is missing.</p>
        `;
        return;
    }

    try {

        const response =
            await authFetch(`http://localhost:8080/api/jobs/${jobId}`);

        if (!response.ok) {
            throw new Error("Job not found");
        }

        const job = await response.json();

        let alreadyApplied = false;
        const currentUser = getCurrentUser();

        if (currentUser?.role === "CANDIDATE" && getAuthToken()) {
            try {
                const applicationsResponse = await authFetch(
                    "http://localhost:8080/api/applications/candidate/me"
                );

                if (applicationsResponse.ok) {
                    const applications = await applicationsResponse.json();
                    alreadyApplied = applications.some(
                        application => String(application.jobId) === String(job.id)
                    );
                }
            } catch (applicationError) {
                console.error("Unable to check application status:", applicationError);
            }
        }

        displayJobDetails(job, alreadyApplied);

    } catch (error) {

        jobDetailsContainer.innerHTML = `
            <p>Unable to load job details.</p>
        `;

        console.error(error);
    }
}


function displayJobDetails(job, alreadyApplied = false) {

    const applyButton = alreadyApplied
        ? `
            <button class="apply-btn applied-btn" disabled>
                Applied
            </button>
          `
        : `
            <button class="apply-btn" onclick="applyForJob(${job.id})">
                Apply for Job
            </button>
          `;

    jobDetailsContainer.innerHTML = `

        <div class="job-details-card">

            <h1>${job.title}</h1>

            <p class="details-company">
                ${job.companyName}
            </p>

            <p class="details-location">
                ${job.location} · ${job.employmentType}
            </p>


            <div class="details-section">

                <h2>Job Description</h2>

                <p>
                    ${job.description}
                </p>

            </div>


            <div class="details-section">

                <h2>Required Skills</h2>

                <p class="details-skills">
                    ${job.skills}
                </p>

            </div>


            <div class="details-bottom">

                <span class="details-salary">
                    ${job.salary}
                </span>

                ${applyButton}

            </div>

        </div>
    `;
}

const registerForm = document.getElementById("register-form");

if (registerForm) {
    registerForm.addEventListener("submit", registerCandidate);
}

async function registerCandidate(event) {

    event.preventDefault();

    const name = document.getElementById("name").value;
    const email = document.getElementById("email").value;
    const password = document.getElementById("password").value;

    const message = document.getElementById("register-message");

    try {

        const response = await authFetch("http://localhost:8080/api/users", {

            method: "POST",

            headers: {
                "Content-Type": "application/json"
            },

            body: JSON.stringify({
                name: name,
                email: email,
                password: password,
                role: "CANDIDATE"
            })
        });

        const data = await response.json();

        if (!response.ok) {

            if (data.errors) {

                const errors = Object.values(data.errors).join("<br>");

                message.innerHTML = errors;

            } else {

                message.textContent =
                    data.message || "Registration failed.";
            }

            return;
        }

        message.textContent =
            "Account created successfully!";

        registerForm.reset();

        setTimeout(() => {
            window.location.href = "login.html";
        }, 800);

    } catch (error) {

        message.textContent =
            "Unable to connect to the server.";

        console.error(error);
    }
}

function registerRecruiter(event) {

    event.preventDefault();

    const form = document.getElementById("recruiter-register-form");

    const name = form.name.value;
    const email = form.email.value;
    const password = form.password.value;

    const message = document.getElementById("recruiter-register-message");

    authFetch("http://localhost:8080/api/users", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({
            name: name,
            email: email,
            password: password,
            role: "RECRUITER"
        })
    })
        .then(response => response.json().then(data => ({
            status: response.status,
            data: data
        })))
        .then(result => {

            const data = result.data;

            if (result.status >= 200 && result.status < 300) {

                message.textContent = "Recruiter registered successfully!";
                form.reset();

                setTimeout(() => {
                    window.location.href = "login.html";
                }, 800);

            } else {

                if (data.errors) {
                    message.textContent = Object.values(data.errors).join(", ");
                } else {
                    message.textContent = data.message || "Registration failed.";
                }
            }
        })
        .catch(error => {
            console.error(error);
            message.textContent = "Unable to connect to the server.";
        });
}

async function applyForJob(jobId) {

    const currentUser = getCurrentUser();

    if (currentUser?.role !== "CANDIDATE") {
        alert("Please log in as a candidate before applying.");
        return;
    }

    try {

        const response = await authFetch(
            `http://localhost:8080/api/applications?jobId=${jobId}`,
            {
                method: "POST"
            }
        );

        const data = await response.json();

        if (!response.ok) {
            if ((data.message || "").toLowerCase().includes("already applied")) {
                setApplicationButtonApplied();
                return;
            }

            alert(data.message || "Unable to apply for this job.");
            return;
        }

        setApplicationButtonApplied();

    } catch (error) {

        alert("Unable to connect to the server.");

        console.error(error);
    }
}

function setApplicationButtonApplied() {
    const button = document.querySelector(".apply-btn");

    if (!button) {
        return;
    }

    button.textContent = "Applied";
    button.disabled = true;
    button.classList.add("applied-btn");
    button.removeAttribute("onclick");
}

const applicationsContainer =
    document.getElementById("applications-container");

if (applicationsContainer) {
    loadApplications();
}

async function loadApplications() {

    const currentUser = getCurrentUser();

    if (currentUser?.role !== "CANDIDATE") {

        applicationsContainer.innerHTML = `
            <p>Please log in as a candidate to view your applications.</p>
        `;

        return;
    }

    try {

        const response = await authFetch(
            `http://localhost:8080/api/applications/candidate/me`
        );

        const applications = await response.json();

        if (!response.ok) {
            throw new Error(
                applications.message || "Failed to load applications"
            );
        }

        displayApplications(applications);

    } catch (error) {

        applicationsContainer.innerHTML = `
            <p>
                Unable to load your applications.
            </p>
        `;

        console.error(error);
    }
}


function displayApplications(applications) {

    applicationsContainer.innerHTML = "";


    if (applications.length === 0) {

        applicationsContainer.innerHTML = `
            <p>
                You haven't applied for any jobs yet.
            </p>
        `;

        return;
    }


    applications.forEach(application => {

        const applicationCard =
            document.createElement("div");


        applicationCard.classList.add(
            "application-card"
        );


        const appliedDate =
            new Date(application.appliedAt)
                .toLocaleDateString();


        let interviewSection = "";


        /*
         * If interview is scheduled,
         * show View Interview button.
         */

        if (application.status === "INTERVIEW_SCHEDULED") {

            interviewSection = `

                <div class="candidate-interview-section">

                    <button
                        type="button"
                        class="view-interview-btn"
                        onclick="viewInterview(${application.id})"
                    >
                        View Interview
                    </button>

                    <div
                        id="interview-details-${application.id}"
                        class="interview-details"
                    >
                    </div>

                </div>

            `;
        }


        applicationCard.innerHTML = `

            <h2>
                ${application.jobTitle}
            </h2>


            <p class="application-company">
                ${application.companyName}
            </p>


            <p class="application-location">
                Application ID: ${application.id}
            </p>


            <div class="application-info">

                <span class="application-status">
                    ${application.status}
                </span>


                <span class="application-date">
                    Applied: ${appliedDate}
                </span>

            </div>


            ${interviewSection}

        `;


        applicationsContainer.appendChild(
            applicationCard
        );

    });
}

function showCreateJobForm() {

    const section = document.getElementById("create-job-section");

    if (section.style.display === "none" || section.style.display === "") {
        section.style.display = "block";
    } else {
        section.style.display = "none";
    }
}

function createJob(event) {

    event.preventDefault();

    const currentUser = getCurrentUser();

    if (currentUser?.role !== "RECRUITER") {
        alert("Please log in as a recruiter first.");
        return;
    }

    const title = document.getElementById("job-title").value;
    const description = document.getElementById("job-description").value;
    const companyName = document.getElementById("company-name").value;
    const location = document.getElementById("job-location").value;
    const employmentType = document.getElementById("employment-type").value;
    const salary = document.getElementById("salary").value;
    const skills = document.getElementById("skills").value;

    const message = document.getElementById("create-job-message");

    authFetch("http://localhost:8080/api/jobs", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({
            title: title,
            description: description,
            companyName: companyName,
            location: location,
            employmentType: employmentType,
            salary: salary,
            skills: skills
        })
    })
        .then(response => response.json().then(data => ({
            status: response.status,
            data: data
        })))
        .then(result => {

            const data = result.data;

            if (result.status >= 200 && result.status < 300) {

                message.textContent = "Job created successfully!";

                document.getElementById("create-job-form").reset();

                loadRecruiterJobs();

            } else {

                if (data.errors) {
                    message.textContent = Object.values(data.errors).join(", ");
                } else {
                    message.textContent = data.message || "Failed to create job.";
                }
            }
        })
        .catch(error => {

            console.error(error);
            message.textContent = "Unable to connect to the server.";

        });
}

function loadRecruiterJobs() {

    const container = document.getElementById("recruiter-jobs-container");

    if (!container) {
        return;
    }

    const currentUser = getCurrentUser();

    if (currentUser?.role !== "RECRUITER") {
        container.innerHTML = "<p>Please log in as a recruiter.</p>";
        return;
    }

    authFetch("http://localhost:8080/api/jobs/recruiter/me")
        .then(response => response.json())
        .then(jobs => {

            if (jobs.length === 0) {
                container.innerHTML = "<p>No job postings available.</p>";
                return;
            }

            container.innerHTML = "";

            jobs.forEach(job => {

                const jobCard = document.createElement("div");

                jobCard.className = "job-card";

                jobCard.innerHTML = `
    <h3>${job.title}</h3>

    <p><strong>Company:</strong> ${job.companyName}</p>

    <p><strong>Location:</strong> ${job.location}</p>

    <p><strong>Employment Type:</strong> ${job.employmentType}</p>

    <p><strong>Salary:</strong> ${job.salary}</p>

    <p><strong>Status:</strong> ${job.status}</p>

    <div class="job-card-actions">
    <a href="job-details.html?id=${job.id}" class="view-job-btn">
        View Job
    </a>

    <a href="recruiter-applications.html?jobId=${job.id}" class="view-applications-btn">
        View Applications
    </a>

    ${
                    job.status === "OPEN"
                        ? `<button class="close-job-btn" onclick="closeJob(${job.id})">
                Close Job
           </button>`
                        : `<span class="closed-job-label">Job Closed</span>`
                }
</div>
`;

                container.appendChild(jobCard);
            });
        })
        .catch(error => {

            console.error(error);

            container.innerHTML =
                "<p>Unable to load job postings.</p>";
        });
}

function showInterviewForm(applicationId) {

    const formContainer =
        document.getElementById(
            `interview-form-${applicationId}`
        );

    formContainer.style.display = "block";

    formContainer.innerHTML = `

        <h3>Schedule Interview</h3>

        <div class="form-group">

            <label>
                Date & Time
            </label>

            <input
                type="datetime-local"
                id="interview-time-${applicationId}"
                required
            >

        </div>


        <div class="form-group">

            <label>
                Interview Mode
            </label>

            <select
                id="interview-mode-${applicationId}"
                onchange="toggleInterviewModeFields(${applicationId})"
            >

                <option value="">
                    Select mode
                </option>

                <option value="Online">
                    Online
                </option>

                <option value="Offline">
                    Offline
                </option>

            </select>

        </div>


        <div class="form-group" id="meeting-link-group-${applicationId}">

            <label>
                Meeting Link
            </label>

            <input
                type="url"
                id="meeting-link-${applicationId}"
                placeholder="https://meet.google.com/..."
            >

        </div>

        <div class="form-group" id="location-group-${applicationId}" style="display: none;">

            <label>
                Interview Location
            </label>

            <input
                type="text"
                id="interview-location-${applicationId}"
                placeholder="Office address or meeting room"
            >

        </div>


        <button
            class="dashboard-btn"
            onclick="scheduleInterview(${applicationId})"
        >
            Schedule Interview
        </button>

    `;
}


function toggleInterviewModeFields(applicationId) {

    const mode = document.getElementById(`interview-mode-${applicationId}`).value;
    const meetingLinkGroup = document.getElementById(`meeting-link-group-${applicationId}`);
    const locationGroup = document.getElementById(`location-group-${applicationId}`);

    if (mode === "Online") {
        meetingLinkGroup.style.display = "block";
        locationGroup.style.display = "none";
    } else if (mode === "Offline") {
        meetingLinkGroup.style.display = "none";
        locationGroup.style.display = "block";
    } else {
        meetingLinkGroup.style.display = "block";
        locationGroup.style.display = "none";
    }
}


async function scheduleInterview(applicationId) {

    const scheduledAt =
        document.getElementById(
            `interview-time-${applicationId}`
        ).value;

    const mode =
        document.getElementById(
            `interview-mode-${applicationId}`
        ).value;

    const meetingLink =
        document.getElementById(
            `meeting-link-${applicationId}`
        ).value.trim();

    const location =
        document.getElementById(
            `interview-location-${applicationId}`
        ).value.trim();


    if (!scheduledAt || !mode) {

        alert(
            "Please select interview date, time and mode."
        );

        return;
    }

    if (mode === "Online" && !meetingLink) {
        alert("Please enter the meeting link for an online interview.");
        return;
    }

    if (mode === "Offline" && !location) {
        alert("Please enter the interview location for an offline interview.");
        return;
    }


    const request = {

        applicationId: applicationId,

        scheduledAt: scheduledAt,

        mode: mode,

        meetingLink:
            mode === "Online" ? meetingLink : null,

        location:
            mode === "Offline" ? location : null

    };


    try {

        const response = await authFetch(
            "http://localhost:8080/api/interviews",
            {
                method: "POST",

                headers: {
                    "Content-Type": "application/json"
                },

                body: JSON.stringify(request)
            }
        );


        const result =
            await response.json();


        if (!response.ok) {

            throw new Error(
                result.message ||
                "Failed to schedule interview"
            );

        }


        alert(
            "Interview scheduled successfully."
        );


        // Reload applications so that
        // status becomes INTERVIEW_SCHEDULED

        loadApplications();


    } catch (error) {

        console.error(error);

        alert(
            error.message ||
            "Failed to schedule interview."
        );

    }

}

async function viewInterview(applicationId) {

    const container =
        document.getElementById(
            `interview-details-${applicationId}`
        );


    if (!container) {
        return;
    }


    container.innerHTML =
        "<p>Loading interview details...</p>";


    try {

        const response = await authFetch(
            `http://localhost:8080/api/interviews/application/${applicationId}`
        );


        const interview =
            await response.json();


        if (!response.ok) {

            throw new Error(
                interview.message ||
                "Interview not found"
            );

        }


        container.innerHTML = `

            <div class="interview-details-card">

                <h3>
                    Interview Details
                </h3>


                <p>
                    <strong>Job:</strong>
                    ${interview.jobTitle}
                </p>


                <p>
                    <strong>Date & Time:</strong>
                    ${formatInterviewDate(
            interview.scheduledAt
        )}
                </p>


                <p>
                    <strong>Mode:</strong>
                    ${interview.mode}
                </p>


                <p>
                    <strong>Status:</strong>
                    ${interview.status}
                </p>


                ${
            interview.meetingLink
                ? `
                        <p>
                            <strong>Meeting Link:</strong>
                            <a
                                href="${interview.meetingLink}"
                                target="_blank"
                            >
                                Join Interview
                            </a>
                        </p>
                    `
                : ""
        }


                ${
            interview.notes
                ? `
                        <p>
                            <strong>Notes:</strong>
                            ${interview.notes}
                        </p>
                    `
                : ""
        }

            </div>

        `;


    } catch (error) {

        console.error(error);


        container.innerHTML = `

            <p>
                Unable to load interview details.
            </p>

        `;

    }

}


function formatInterviewDate(dateTime) {

    if (!dateTime) {
        return "N/A";
    }


    return new Date(dateTime)
        .toLocaleString();

}

function closeJob(jobId) {

    const currentUser = getCurrentUser();

    if (currentUser?.role !== "RECRUITER") {
        alert("Please log in as a recruiter first.");
        return;
    }

    const confirmed = confirm(
        "Are you sure you want to close this job? Candidates will no longer be able to apply."
    );

    if (!confirmed) {
        return;
    }

    authFetch(
        `http://localhost:8080/api/jobs/${jobId}/close`,
        {
            method: "PATCH"
        }
    )
        .then(response => {

            if (!response.ok) {
                return response.text().then(message => {
                    throw new Error(message);
                });
            }

            return response.json();
        })
        .then(updatedJob => {

            alert("Job closed successfully.");

            loadRecruiterJobs();
        })
        .catch(error => {

            console.error("Error closing job:", error);

            alert("Failed to close job: " + error.message);
        });
}
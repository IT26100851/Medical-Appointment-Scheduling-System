/**
 * Medical Appointment Scheduling System - Client Application JavaScript
 * SE1020 Object Oriented Programming Project
 * Handles API calls, dynamic view routing, state management, and form submissions.
 */

// Application State
const state = {
    user: null, // { userId, username, name, role: 'PATIENT'|'ADMIN'|'STAFF', email, patientType }
    currentView: 'home',
    doctors: [],
    patients: [],
    appointments: [],
    payments: [],
    staff: [],
    reviews: []
};

// Base API URL
const API_BASE = '/api';

// Initialize Application on DOM Ready with Dynamic Modular Parts
document.addEventListener('DOMContentLoaded', () => {
    initializeAppModules();
});

const PART_VIEWS = [
    { id: 'view-home', path: '/parts/home.html' },
    { id: 'view-register', path: '/parts/register.html' },
    { id: 'view-login', path: '/parts/login.html' },
    { id: 'view-patient-dashboard', path: '/parts/patient-dashboard.html' },
    { id: 'view-patient-profile', path: '/parts/patient-profile.html' },
    { id: 'view-doctor-listing', path: '/parts/doctor-listing.html' },
    { id: 'view-appointment-booking', path: '/parts/appointment-booking.html' },
    { id: 'view-appointment-list', path: '/parts/patient-appointments.html' },
    { id: 'view-payment-page', path: '/parts/payment-page.html' },
    { id: 'view-payment-history', path: '/parts/payment-history.html' },
    { id: 'view-review-submit', path: '/parts/review-submit.html' },
    { id: 'view-review-list', path: '/parts/review-listing.html' },
    { id: 'view-staff-dashboard', path: '/parts/staff-dashboard.html' },
    { id: 'view-admin-dashboard', path: '/parts/admin-dashboard.html' },
    { id: 'view-patient-management', path: '/parts/admin-patients.html' },
    { id: 'view-doctor-management', path: '/parts/admin-doctors.html' },
    { id: 'view-staff-management', path: '/parts/admin-staff.html' },
    { id: 'view-appointment-management', path: '/parts/admin-appointments.html' },
    { id: 'view-payment-management', path: '/parts/admin-payments.html' },
    { id: 'view-review-management', path: '/parts/admin-reviews.html' }
];

async function loadHTMLPart(url) {
    const res = await fetch(url);
    if (!res.ok) throw new Error(`HTTP ${res.status} fetching ${url}`);
    return await res.text();
}

async function initializeAppModules() {
    try {
        const [navHtml, footerHtml, modalsHtml] = await Promise.all([
            loadHTMLPart('/parts/navbar.html'),
            loadHTMLPart('/parts/footer.html'),
            loadHTMLPart('/parts/modals.html')
        ]);

        const navContainer = document.getElementById('navbar-container');
        const footerContainer = document.getElementById('footer-container');
        const modalsContainer = document.getElementById('modals-container');

        if (navContainer) navContainer.innerHTML = navHtml;
        if (footerContainer) footerContainer.innerHTML = footerHtml;
        if (modalsContainer) modalsContainer.innerHTML = modalsHtml;

        const viewHtmls = await Promise.all(
            PART_VIEWS.map(v => loadHTMLPart(v.path))
        );

        const mainContent = document.getElementById('main-content');
        if (mainContent) {
            mainContent.innerHTML = viewHtmls.join('\n');
        }

        initSession();
        initRouter();
        setupEventListeners();
        updateUIForRole();
    } catch (e) {
        console.error('Initialization error loading parts:', e);
        const mainContent = document.getElementById('main-content');
        if (mainContent) {
            mainContent.innerHTML = `<div class="alert alert-danger my-5 text-center"><h5>Failed to load components</h5><p class="mb-0 text-muted">${e.message}</p></div>`;
        }
    }
}

// ==========================================
// Session & Role Management
// ==========================================
function initSession() {
    const saved = localStorage.getItem('med_user_session');
    if (saved) {
        try {
            state.user = JSON.parse(saved);
        } catch (e) {
            state.user = null;
        }
    } else {
        // Default guest or pre-set to regular patient for easy viva demo
        state.user = {
            userId: 'PAT-101',
            username: 'john_doe',
            name: 'John Doe',
            role: 'PATIENT',
            email: 'john.doe@example.com',
            patientType: 'VIP'
        };
        localStorage.setItem('med_user_session', JSON.stringify(state.user));
    }
}

function setSession(userObj) {
    state.user = userObj;
    if (userObj) {
        localStorage.setItem('med_user_session', JSON.stringify(userObj));
    } else {
        localStorage.removeItem('med_user_session');
    }
    updateUIForRole();
    if (!state.user) {
        navigateTo('home');
    } else if (state.user.role === 'ADMIN') {
        navigateTo('admin-dashboard');
    } else if (state.user.role === 'STAFF') {
        navigateTo('staff-dashboard');
    } else {
        navigateTo('patient-dashboard');
    }
}

function quickSwitchRole(role) {
    if (role === 'PATIENT_VIP') {
        setSession({
            userId: 'PAT-101',
            username: 'john_doe',
            name: 'John Doe',
            role: 'PATIENT',
            email: 'john.doe@example.com',
            patientType: 'VIP'
        });
        showToast('Switched to Patient: John Doe (VIP Gold)', 'info');
    } else if (role === 'PATIENT_REGULAR') {
        setSession({
            userId: 'PAT-102',
            username: 'sarah_smith',
            name: 'Sarah Smith',
            role: 'PATIENT',
            email: 'sarah.smith@example.com',
            patientType: 'Regular'
        });
        showToast('Switched to Patient: Sarah Smith (Regular)', 'info');
    } else if (role === 'ADMIN') {
        setSession({
            userId: 'ADM-001',
            username: 'admin',
            name: 'System Administrator',
            role: 'ADMIN',
            email: 'admin@hospital.com',
            patientType: null
        });
        showToast('Switched to Role: Administrator', 'warning');
    } else if (role === 'STAFF') {
        setSession({
            userId: 'STF-101',
            username: 'staff',
            name: 'Nurse Mary Watson',
            role: 'STAFF',
            email: 'staff@hospital.com',
            patientType: null
        });
        showToast('Switched to Role: Medical Staff', 'info');
    } else {
        setSession(null);
        showToast('Logged out to Guest Mode', 'secondary');
    }
}

function updateUIForRole() {
    const role = state.user ? state.user.role : 'GUEST';
    const userNameSpan = document.getElementById('current-user-display');
    const roleBadge = document.getElementById('current-role-badge');
    
    if (userNameSpan && roleBadge) {
        if (state.user) {
            userNameSpan.textContent = state.user.name + (state.user.patientType ? ` (${state.user.patientType})` : '');
            roleBadge.textContent = state.user.role;
            roleBadge.className = 'badge ' + (role === 'ADMIN' ? 'bg-danger' : role === 'STAFF' ? 'bg-info text-dark' : 'badge-vip');
        } else {
            userNameSpan.textContent = 'Guest User';
            roleBadge.textContent = 'GUEST';
            roleBadge.className = 'badge bg-secondary';
        }
    }

    // Role-specific nav elements
    document.querySelectorAll('.role-nav').forEach(el => {
        const allowedRoles = el.getAttribute('data-roles').split(',');
        if (allowedRoles.includes(role)) {
            el.classList.remove('d-none');
        } else {
            el.classList.add('d-none');
        }
    });

    const roleSelector = document.getElementById('role-quick-select');
    if (roleSelector) {
        if (!state.user) roleSelector.value = 'GUEST';
        else if (state.user.role === 'ADMIN') roleSelector.value = 'ADMIN';
        else if (state.user.role === 'STAFF') roleSelector.value = 'STAFF';
        else if (state.user.patientType === 'VIP') roleSelector.value = 'PATIENT_VIP';
        else roleSelector.value = 'PATIENT_REGULAR';
    }
}

// ==========================================
// Router & View Management (21 Views)
// ==========================================
function initRouter() {
    window.addEventListener('hashchange', () => {
        const hash = window.location.hash.replace('#', '') || 'home';
        navigateTo(hash, false);
    });

    const initial = window.location.hash.replace('#', '') || 'home';
    navigateTo(initial, false);
}

function navigateTo(viewName, updateHash = true) {
    // Role-based Route Protection Guards
    if (viewName === 'admin-dashboard' || viewName.startsWith('admin-') || viewName === 'staff-management' || viewName === 'doctor-management' || viewName === 'patient-management' || viewName === 'payment-management' || viewName === 'review-management') {
        if (!state.user || state.user.role !== 'ADMIN') {
            if (state.user && state.user.role === 'STAFF') {
                showToast('Access restricted: Staff cannot view Admin Dashboard/Controls.', 'warning');
                navigateTo('staff-dashboard');
                return;
            } else if (state.user && state.user.role === 'PATIENT') {
                showToast('Access restricted: Administrator privileges required.', 'warning');
                navigateTo('patient-dashboard');
                return;
            } else {
                showToast('Please login as Administrator.', 'warning');
                navigateTo('login');
                return;
            }
        }
    }

    if (viewName === 'staff-dashboard') {
        if (!state.user || (state.user.role !== 'STAFF' && state.user.role !== 'ADMIN')) {
            showToast('Please login with Medical Staff credentials.', 'warning');
            navigateTo('login');
            return;
        }
    }

    if (viewName === 'patient-dashboard' || viewName === 'patient-profile' || viewName === 'appointment-list') {
        if (!state.user) {
            showToast('Please login to access your patient portal.', 'info');
            navigateTo('login');
            return;
        }
    }

    state.currentView = viewName;
    if (updateHash) {
        window.location.hash = '#' + viewName;
    }

    // Hide all view containers
    document.querySelectorAll('.view-section').forEach(sec => {
        sec.classList.add('d-none');
    });

    // Show target view
    const target = document.getElementById('view-' + viewName);
    if (target) {
        target.classList.remove('d-none');
    } else {
        const homeSec = document.getElementById('view-home');
        if (homeSec) homeSec.classList.remove('d-none');
    }

    // Update active class on nav links
    document.querySelectorAll('.nav-link').forEach(link => {
        if (link.getAttribute('href') === '#' + viewName) {
            link.classList.add('active');
        } else {
            link.classList.remove('active');
        }
    });

    // Trigger view-specific data loader
    loadViewData(viewName);
    window.scrollTo({ top: 0, behavior: 'smooth' });
}

function loadViewData(view) {
    switch (view) {
        case 'home':
            loadHomeDoctors();
            loadHomeReviews();
            break;
        case 'patient-dashboard':
            loadPatientDashboard();
            break;
        case 'patient-profile':
            loadPatientProfile();
            break;
        case 'doctor-listing':
        case 'doctor-search':
            loadDoctorsList();
            break;
        case 'appointment-booking':
            loadBookingDoctors();
            break;
        case 'appointment-list':
            loadPatientAppointments();
            break;
        case 'payment-page':
            setupPaymentPage();
            break;
        case 'payment-history':
            loadPaymentHistory();
            break;
        case 'review-submit':
            loadReviewDoctors();
            break;
        case 'review-list':
            loadAllReviews();
            break;
        case 'staff-dashboard':
            loadStaffDashboard();
            break;
        case 'admin-dashboard':
            loadAdminDashboard();
            break;
        case 'patient-management':
            loadAdminPatients();
            break;
        case 'doctor-management':
            loadAdminDoctors();
            break;
        case 'staff-management':
            loadAdminStaff();
            break;
        case 'appointment-management':
            loadAdminAppointments();
            break;
        case 'payment-management':
            loadAdminPayments();
            break;
        case 'review-management':
            loadAdminReviews();
            break;
    }
}

// ==========================================
// View: Home
// ==========================================
async function loadHomeDoctors() {
    try {
        const res = await fetch(`${API_BASE}/doctors`);
        const json = await res.json();
        if (json.success) {
            const container = document.getElementById('home-doctors-container');
            if (!container) return;
            container.innerHTML = json.data.slice(0, 3).map(doc => `
                <div class="col-md-4 mb-4">
                    <div class="card h-100 p-3 shadow-sm border-0">
                        <div class="d-flex align-items-center mb-3">
                            <div class="stat-icon bg-primary text-white me-3">
                                <i class="bi bi-person-badge"></i>
                            </div>
                            <div>
                                <h6 class="mb-0 fw-bold">${doc.name}</h6>
                                <small class="text-muted">${doc.specialization}</small>
                            </div>
                        </div>
                        <p class="text-muted small">${doc.doctorType} • LKR ${doc.consultationFee.toFixed(2)}</p>
                        <div class="mt-auto d-flex justify-content-between align-items-center">
                            <span class="badge ${doc.availabilityStatus === 'Available' ? 'bg-success' : 'bg-warning text-dark'}">${doc.availabilityStatus}</span>
                            <button class="btn btn-sm btn-teal" onclick="startBookingDoctor('${doc.doctorId}')">Book Now</button>
                        </div>
                    </div>
                </div>
            `).join('');
        }
    } catch (e) {
        console.error(e);
    }
}

async function loadHomeReviews() {
    try {
        const res = await fetch(`${API_BASE}/reviews`);
        const json = await res.json();
        if (json.success) {
            const container = document.getElementById('home-reviews-container');
            if (!container) return;
            container.innerHTML = json.data.slice(0, 3).map(rev => `
                <div class="col-md-4 mb-3">
                    <div class="card h-100 p-3 border-0 shadow-sm bg-white">
                        <div class="d-flex justify-content-between align-items-center mb-2">
                            <span class="text-warning">${'★'.repeat(rev.rating)}${'☆'.repeat(5 - rev.rating)}</span>
                            <span class="badge ${rev.reviewType === 'VerifiedReview' ? 'badge-verified' : 'bg-light text-dark'}">${rev.reviewType === 'VerifiedReview' ? 'Verified Patient' : 'Patient Review'}</span>
                        </div>
                        <p class="small text-secondary fst-italic">"${rev.comment}"</p>
                        <small class="text-muted text-end d-block">- Patient ${rev.patientId} on ${rev.date}</small>
                    </div>
                </div>
            `).join('');
        }
    } catch (e) {
        console.error(e);
    }
}

// ==========================================
// View: Patient Dashboard
// ==========================================
async function loadPatientDashboard() {
    if (!state.user || state.user.role !== 'PATIENT') {
        navigateTo('login');
        return;
    }

    document.getElementById('dash-patient-name').textContent = state.user.name;
    document.getElementById('dash-patient-tier').textContent = state.user.patientType === 'VIP' ? 'VIP Gold Member (15% Off Consultations)' : 'Regular Member';

    try {
        const res = await fetch(`${API_BASE}/dashboard/patient-stats/${state.user.userId}`);
        const json = await res.json();
        if (json.success) {
            const data = json.data;
            document.getElementById('dash-upcoming-count').textContent = data.upcomingAppointments.length;
            document.getElementById('dash-past-count').textContent = data.pastAppointments.length;
            document.getElementById('dash-payments-count').textContent = data.payments.length;

            // Render upcoming list
            const upContainer = document.getElementById('dash-upcoming-list');
            if (upContainer) {
                if (data.upcomingAppointments.length === 0) {
                    upContainer.innerHTML = '<div class="text-muted p-3">No upcoming appointments scheduled.</div>';
                } else {
                    upContainer.innerHTML = data.upcomingAppointments.map(a => `
                        <div class="card mb-2 p-3 border-start border-4 border-primary">
                            <div class="d-flex justify-content-between align-items-center">
                                <div>
                                    <h6 class="mb-1 fw-bold">Appointment #${a.appointmentId} - Doctor: ${a.doctorId}</h6>
                                    <div class="small text-muted"><i class="bi bi-calendar3 me-1"></i>${a.appointmentDate} at ${a.appointmentTime} | Type: ${a.appointmentType}</div>
                                    <div class="small text-secondary mt-1">Reason: ${a.reason}</div>
                                </div>
                                <div class="text-end">
                                    <span class="badge bg-primary mb-2">${a.status}</span><br/>
                                    <button class="btn btn-sm btn-outline-danger" onclick="cancelAppointmentPrompt('${a.appointmentId}')">Cancel</button>
                                </div>
                            </div>
                        </div>
                    `).join('');
                }
            }

            // Render recent payments
            const payContainer = document.getElementById('dash-recent-payments');
            if (payContainer) {
                if (data.payments.length === 0) {
                    payContainer.innerHTML = '<div class="text-muted p-3">No payment records found.</div>';
                } else {
                    payContainer.innerHTML = data.payments.slice(0, 3).map(p => `
                        <div class="d-flex justify-content-between align-items-center border-bottom py-2">
                            <div>
                                <strong class="small">${p.paymentId}</strong>
                                <div class="text-muted small">${p.paymentMethod} • ${p.paymentDate}</div>
                            </div>
                            <div class="text-end">
                                <span class="fw-bold text-success">LKR ${p.amount.toFixed(2)}</span>
                                <span class="badge bg-success ms-1">${p.paymentStatus}</span>
                            </div>
                        </div>
                    `).join('');
                }
            }
        }
    } catch (e) {
        console.error(e);
    }
}

// ==========================================
// View: Patient Profile
// ==========================================
async function loadPatientProfile() {
    if (!state.user || state.user.role !== 'PATIENT') {
        navigateTo('login');
        return;
    }

    try {
        const res = await fetch(`${API_BASE}/patients/${state.user.userId}`);
        const json = await res.json();
        if (json.success) {
            const p = json.data;
            document.getElementById('prof-id').value = p.patientId;
            document.getElementById('prof-username').value = p.username;
            document.getElementById('prof-name').value = p.name;
            document.getElementById('prof-email').value = p.email;
            document.getElementById('prof-phone').value = p.phone;
            document.getElementById('prof-address').value = p.address;
            document.getElementById('prof-type').value = p.patientType;
        }
    } catch (e) {
        showToast('Error loading profile: ' + e.message, 'danger');
    }
}

async function handleProfileUpdate(e) {
    e.preventDefault();
    const id = document.getElementById('prof-id').value;
    const body = {
        name: document.getElementById('prof-name').value,
        email: document.getElementById('prof-email').value,
        phone: document.getElementById('prof-phone').value,
        address: document.getElementById('prof-address').value,
        patientType: document.getElementById('prof-type').value,
        password: document.getElementById('prof-password').value || undefined
    };

    try {
        const res = await fetch(`${API_BASE}/patients/${id}`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(body)
        });
        const json = await res.json();
        if (json.success) {
            showToast('Profile updated successfully!', 'success');
            state.user.name = body.name;
            state.user.email = body.email;
            state.user.patientType = body.patientType;
            setSession(state.user);
        } else {
            showToast(json.message, 'danger');
        }
    } catch (e) {
        showToast('Update failed: ' + e.message, 'danger');
    }
}

// ==========================================
// View: Doctors (Listing & Search)
// ==========================================
async function loadDoctorsList(specFilter = '', availFilter = '', searchKeyword = '') {
    try {
        let url = `${API_BASE}/doctors?`;
        if (specFilter) url += `specialization=${encodeURIComponent(specFilter)}&`;
        if (availFilter) url += `availability=${encodeURIComponent(availFilter)}&`;
        if (searchKeyword) url += `search=${encodeURIComponent(searchKeyword)}&`;

        const res = await fetch(url);
        const json = await res.json();
        if (json.success) {
            state.doctors = json.data;
            renderDoctorsGrid(json.data);
        }
    } catch (e) {
        console.error(e);
    }
}

function renderDoctorsGrid(doctors) {
    const container = document.getElementById('doctors-grid');
    if (!container) return;

    if (doctors.length === 0) {
        container.innerHTML = '<div class="col-12 text-center py-5 text-muted">No doctors found matching criteria.</div>';
        return;
    }

    container.innerHTML = doctors.map(doc => `
        <div class="col-md-6 col-lg-4 mb-4">
            <div class="card h-100 shadow-sm border-0">
                <div class="card-body">
                    <div class="d-flex align-items-center mb-3">
                        <div class="stat-icon bg-primary text-white me-3">
                            <i class="bi bi-heart-pulse"></i>
                        </div>
                        <div>
                            <h5 class="card-title mb-0 fw-bold">${doc.name}</h5>
                            <span class="badge badge-doctor">${doc.specialization}</span>
                        </div>
                    </div>
                    <ul class="list-unstyled small text-muted mb-3">
                        <li><i class="bi bi-award me-2"></i>Type: <strong>${doc.doctorType}</strong></li>
                        <li><i class="bi bi-telephone me-2"></i>Contact: ${doc.contactNumber}</li>
                        <li><i class="bi bi-cash-stack me-2"></i>Fee: <strong>LKR ${doc.consultationFee.toFixed(2)}</strong></li>
                        <li><i class="bi bi-info-circle me-2"></i>Role: ${doc.roleDescription || 'Clinical Consultant'}</li>
                    </ul>
                    <div class="d-flex justify-content-between align-items-center pt-2 border-top">
                        <span class="badge ${doc.availabilityStatus === 'Available' ? 'bg-success' : 'bg-warning text-dark'}">${doc.availabilityStatus}</span>
                        <button class="btn btn-sm btn-teal" onclick="startBookingDoctor('${doc.doctorId}')">
                            <i class="bi bi-calendar-plus me-1"></i>Book
                        </button>
                    </div>
                </div>
            </div>
        </div>
    `).join('');
}

function startBookingDoctor(doctorId) {
    navigateTo('appointment-booking');
    setTimeout(() => {
        const select = document.getElementById('book-doctor-id');
        if (select) {
            select.value = doctorId;
        }
    }, 200);
}

// ==========================================
// View: Appointment Booking
// ==========================================
async function loadBookingDoctors() {
    try {
        const res = await fetch(`${API_BASE}/doctors`);
        const json = await res.json();
        if (json.success) {
            const select = document.getElementById('book-doctor-id');
            if (select) {
                select.innerHTML = '<option value="">-- Choose a Doctor --</option>' +
                    json.data.map(d => `<option value="${d.doctorId}">${d.name} (${d.specialization} - LKR ${d.consultationFee.toFixed(2)}) [${d.availabilityStatus}]</option>`).join('');
            }
        }

        // Fill patient ID if logged in
        const patientInput = document.getElementById('book-patient-id');
        if (patientInput && state.user && state.user.role === 'PATIENT') {
            patientInput.value = state.user.userId;
        }

        // Set minimum and default appointment date (Past dates cannot be selected)
        const todayStr = new Date().toISOString().split('T')[0];
        const dateInput = document.getElementById('book-date');
        if (dateInput) {
            dateInput.min = todayStr; // Disable past dates in datepicker
            if (!dateInput.value || dateInput.value < todayStr) {
                const tomorrow = new Date();
                tomorrow.setDate(tomorrow.getDate() + 1);
                dateInput.value = tomorrow.toISOString().split('T')[0];
            }
        }
    } catch (e) {
        console.error(e);
    }
}

async function checkBookingAvailability() {
    const doctorId = document.getElementById('book-doctor-id').value;
    const date = document.getElementById('book-date').value;
    const time = document.getElementById('book-time').value;
    const feedback = document.getElementById('booking-availability-feedback');
    const todayStr = new Date().toISOString().split('T')[0];

    if (!doctorId || !date || !time) {
        if (feedback) feedback.innerHTML = '<span class="text-warning">Please select Doctor, Date, and Time first.</span>';
        return;
    }

    if (date < todayStr) {
        if (feedback) feedback.innerHTML = '<span class="text-danger"><i class="bi bi-exclamation-triangle-fill me-1"></i>Cannot select past dates. Please pick today or a future date.</span>';
        return;
    }

    try {
        const res = await fetch(`${API_BASE}/appointments/check-availability?doctorId=${doctorId}&date=${date}&time=${time}`);
        const json = await res.json();
        if (feedback) {
            if (json.success && json.data === true) {
                feedback.innerHTML = '<span class="text-success"><i class="bi bi-check-circle-fill me-1"></i>Slot is Available! Doctor is free.</span>';
            } else {
                feedback.innerHTML = '<span class="text-danger"><i class="bi bi-x-circle-fill me-1"></i>Doctor is NOT available at this slot. Please pick another.</span>';
            }
        }
    } catch (e) {
        if (feedback) feedback.innerHTML = '<span class="text-danger">Error checking availability.</span>';
    }
}

async function handleAppointmentBooking(e) {
    e.preventDefault();
    const aptDate = document.getElementById('book-date').value;
    const todayStr = new Date().toISOString().split('T')[0];

    if (!aptDate || aptDate < todayStr) {
        showToast('Appointment date cannot be in the past. Please select today or a future date.', 'danger');
        return;
    }

    const body = {
        patientId: document.getElementById('book-patient-id').value,
        doctorId: document.getElementById('book-doctor-id').value,
        appointmentDate: aptDate,
        appointmentTime: document.getElementById('book-time').value,
        appointmentType: document.getElementById('book-type').value,
        reason: document.getElementById('book-reason').value
    };

    try {
        const res = await fetch(`${API_BASE}/appointments/book`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(body)
        });
        const json = await res.json();
        if (json.success) {
            showToast('Appointment booked successfully! ID: ' + json.data.appointmentId, 'success');
            // Navigate to payment or appointment list
            setTimeout(() => {
                promptGoToPayment(json.data.appointmentId, json.data.patientId, json.data.doctorId);
            }, 800);
        } else {
            showToast(json.message, 'danger');
        }
    } catch (e) {
        showToast('Booking failed: ' + e.message, 'danger');
    }
}

function promptGoToPayment(aptId, patientId, doctorId) {
    if (confirm('Appointment booked successfully! Would you like to proceed to make payment now?')) {
        navigateTo('payment-page');
        setTimeout(() => {
            const aptSelect = document.getElementById('pay-appointment-id');
            if (aptSelect) aptSelect.value = aptId;
            const patInput = document.getElementById('pay-patient-id');
            if (patInput) patInput.value = patientId;
            updatePaymentAmountPreview(aptId);
        }, 300);
    } else {
        navigateTo('appointment-list');
    }
}

// ==========================================
// View: Appointment List & Cancel / Reschedule
// ==========================================
async function loadPatientAppointments() {
    if (!state.user || state.user.role !== 'PATIENT') {
        navigateTo('login');
        return;
    }

    try {
        const res = await fetch(`${API_BASE}/appointments/patient/${state.user.userId}`);
        const json = await res.json();
        if (json.success) {
            const tbody = document.getElementById('patient-appointments-tbody');
            if (!tbody) return;

            if (json.data.length === 0) {
                tbody.innerHTML = '<tr><td colspan="7" class="text-center text-muted py-4">No appointments recorded yet.</td></tr>';
                return;
            }

            tbody.innerHTML = json.data.map(a => `
                <tr>
                    <td><strong>${a.appointmentId}</strong></td>
                    <td>${a.doctorId}</td>
                    <td>${a.appointmentDate}</td>
                    <td>${a.appointmentTime}</td>
                    <td><span class="badge ${a.appointmentType === 'Emergency' ? 'bg-danger' : a.appointmentType === 'FollowUp' ? 'bg-info text-dark' : 'bg-primary'}">${a.appointmentType}</span></td>
                    <td>
                        <span class="badge ${a.status === 'Completed' ? 'bg-success' : a.status === 'Cancelled' ? 'bg-secondary' : 'bg-warning text-dark'}">${a.status}</span>
                    </td>
                    <td>
                        ${a.status === 'Scheduled' ? `
                            <button class="btn btn-sm btn-outline-primary me-1" onclick="openRescheduleModal('${a.appointmentId}', '${a.appointmentDate}', '${a.appointmentTime}', '${a.reason}')">
                                <i class="bi bi-pencil"></i>
                            </button>
                            <button class="btn btn-sm btn-outline-danger me-1" onclick="cancelAppointmentPrompt('${a.appointmentId}')">
                                <i class="bi bi-x-circle"></i>
                            </button>
                            <button class="btn btn-sm btn-success" onclick="goToPaymentForAppointment('${a.appointmentId}', '${a.patientId}')">
                                Pay
                            </button>
                        ` : a.status === 'Completed' ? `
                            <button class="btn btn-sm btn-outline-info" onclick="goToReviewDoctor('${a.doctorId}')">Review Doctor</button>
                        ` : `<span class="text-muted small">None</span>`}
                    </td>
                </tr>
            `).join('');
        }
    } catch (e) {
        console.error(e);
    }
}

async function cancelAppointmentPrompt(aptId) {
    if (!confirm(`Are you sure you want to cancel appointment ${aptId}?`)) return;

    try {
        const res = await fetch(`${API_BASE}/appointments/${aptId}/cancel`, {
            method: 'PUT'
        });
        const json = await res.json();
        if (json.success) {
            showToast('Appointment cancelled.', 'info');
            loadPatientAppointments();
            if (state.currentView === 'patient-dashboard') loadPatientDashboard();
        } else {
            showToast(json.message, 'danger');
        }
    } catch (e) {
        showToast('Cancellation error: ' + e.message, 'danger');
    }
}

function openRescheduleModal(aptId, date, time, reason) {
    const todayStr = new Date().toISOString().split('T')[0];
    const dateInput = document.getElementById('modal-reschedule-date');
    if (dateInput) {
        dateInput.min = todayStr; // Disable past dates
        dateInput.value = (date && date >= todayStr) ? date : todayStr;
    }
    document.getElementById('modal-reschedule-id').value = aptId;
    document.getElementById('modal-reschedule-time').value = time;
    document.getElementById('modal-reschedule-reason').value = reason;

    const modal = new bootstrap.Modal(document.getElementById('rescheduleModal'));
    modal.show();
}

async function handleAppointmentRescheduleSubmit(e) {
    e.preventDefault();
    const aptId = document.getElementById('modal-reschedule-id').value;
    const reschedDate = document.getElementById('modal-reschedule-date').value;
    const todayStr = new Date().toISOString().split('T')[0];

    if (!reschedDate || reschedDate < todayStr) {
        showToast('Rescheduled date cannot be in the past. Please select today or a future date.', 'danger');
        return;
    }

    const body = {
        appointmentDate: reschedDate,
        appointmentTime: document.getElementById('modal-reschedule-time').value,
        reason: document.getElementById('modal-reschedule-reason').value
    };

    try {
        const res = await fetch(`${API_BASE}/appointments/${aptId}`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(body)
        });
        const json = await res.json();
        if (json.success) {
            showToast('Appointment rescheduled successfully!', 'success');
            bootstrap.Modal.getInstance(document.getElementById('rescheduleModal')).hide();
            loadPatientAppointments();
        } else {
            showToast(json.message, 'danger');
        }
    } catch (e) {
        showToast('Reschedule error: ' + e.message, 'danger');
    }
}

// ==========================================
// View: Payment Page & History
// ==========================================
async function setupPaymentPage() {
    if (state.user && state.user.role === 'PATIENT') {
        document.getElementById('pay-patient-id').value = state.user.userId;

        // Load patient's appointments into the dropdown
        try {
            const res = await fetch(`${API_BASE}/appointments/patient/${state.user.userId}`);
            const json = await res.json();
            if (json.success) {
                const select = document.getElementById('pay-appointment-id');
                select.innerHTML = '<option value="">-- Select Appointment to Pay --</option>' +
                    json.data.map(a => `<option value="${a.appointmentId}">${a.appointmentId} (${a.appointmentDate} - Doctor: ${a.doctorId}) [${a.status}]</option>`).join('');
            }
        } catch (e) {
            console.error(e);
        }
    }
}

function goToPaymentForAppointment(aptId, patId) {
    navigateTo('payment-page');
    setTimeout(() => {
        document.getElementById('pay-appointment-id').value = aptId;
        document.getElementById('pay-patient-id').value = patId;
        updatePaymentAmountPreview(aptId);
    }, 200);
}

async function updatePaymentAmountPreview(aptId) {
    if (!aptId) return;
    try {
        const aptRes = await fetch(`${API_BASE}/appointments/${aptId}`);
        const aptJson = await aptRes.json();
        if (aptJson.success) {
            const docId = aptJson.data.doctorId;
            const docRes = await fetch(`${API_BASE}/doctors/${docId}`);
            const docJson = await docRes.json();
            if (docJson.success) {
                const baseFee = docJson.data.consultationFee;
                let discount = 0.0;
                if (state.user && state.user.patientType === 'VIP') {
                    discount = baseFee * 0.15; // 15% VIP discount
                }

                document.getElementById('pay-base-fee').textContent = 'LKR ' + baseFee.toFixed(2);
                document.getElementById('pay-discount-amount').textContent = '- LKR ' + discount.toFixed(2);
                document.getElementById('pay-total-amount').textContent = 'LKR ' + (baseFee - discount).toFixed(2);
                document.getElementById('pay-amount-input').value = (baseFee - discount).toFixed(2);
            }
        }
    } catch (e) {
        console.error(e);
    }
}

async function handlePaymentSubmit(e) {
    e.preventDefault();
    const aptId = document.getElementById('pay-appointment-id').value;
    const patId = document.getElementById('pay-patient-id').value;
    const amount = parseFloat(document.getElementById('pay-amount-input').value);
    const method = document.getElementById('pay-method').value;
    const detail = document.getElementById('pay-detail-input').value;

    if (!aptId || !patId || isNaN(amount) || amount <= 0) {
        showToast('Please verify payment details.', 'warning');
        return;
    }

    try {
        const res = await fetch(`${API_BASE}/payments/process`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                appointmentId: aptId,
                patientId: patId,
                amount: amount,
                method: method,
                detail: detail
            })
        });
        const json = await res.json();
        if (json.success) {
            showToast('Payment processed successfully! Receipt: ' + json.data.paymentId, 'success');
            showReceiptModal(json.data);
            navigateTo('payment-history');
        } else {
            showToast(json.message, 'danger');
        }
    } catch (e) {
        showToast('Payment processing error: ' + e.message, 'danger');
    }
}

function showReceiptModal(payment) {
    const content = document.getElementById('receipt-modal-content');
    if (!content) return;
    content.innerHTML = `
        <div class="p-3">
            <div class="text-center mb-3">
                <i class="bi bi-check-circle-fill text-success" style="font-size: 3rem;"></i>
                <h5 class="fw-bold mt-2">Hospital Payment Receipt</h5>
                <small class="text-muted">Transaction #${payment.transactionReference || 'TXN-991'}</small>
            </div>
            <table class="table table-bordered small">
                <tr><th>Payment ID</th><td>${payment.paymentId}</td></tr>
                <tr><th>Appointment ID</th><td>${payment.appointmentId}</td></tr>
                <tr><th>Patient ID</th><td>${payment.patientId}</td></tr>
                <tr><th>Amount Paid</th><td><strong>LKR ${payment.amount.toFixed(2)}</strong></td></tr>
                <tr><th>Payment Method</th><td>${payment.paymentMethod}</td></tr>
                <tr><th>Receipt Details</th><td>${payment.paymentReceiptDetails || payment.paymentMethod}</td></tr>
                <tr><th>Date</th><td>${payment.paymentDate}</td></tr>
                <tr><th>Status</th><td><span class="badge bg-success">${payment.paymentStatus}</span></td></tr>
            </table>
        </div>
    `;
    const modal = new bootstrap.Modal(document.getElementById('receiptModal'));
    modal.show();
}

async function loadPaymentHistory() {
    if (!state.user || state.user.role !== 'PATIENT') {
        navigateTo('login');
        return;
    }

    try {
        const res = await fetch(`${API_BASE}/payments/patient/${state.user.userId}`);
        const json = await res.json();
        if (json.success) {
            const tbody = document.getElementById('payment-history-tbody');
            if (!tbody) return;

            if (json.data.length === 0) {
                tbody.innerHTML = '<tr><td colspan="7" class="text-center text-muted py-4">No payment history found.</td></tr>';
                return;
            }

            tbody.innerHTML = json.data.map(p => `
                <tr>
                    <td><strong>${p.paymentId}</strong></td>
                    <td>${p.appointmentId}</td>
                    <td>LKR ${p.amount.toFixed(2)}</td>
                    <td>${p.paymentMethod}</td>
                    <td>${p.paymentDate}</td>
                    <td><span class="badge bg-success">${p.paymentStatus}</span></td>
                    <td>
                        <button class="btn btn-sm btn-outline-secondary" onclick='showReceiptModal(${JSON.stringify(p)})'>
                            <i class="bi bi-receipt"></i> Receipt
                        </button>
                    </td>
                </tr>
            `).join('');
        }
    } catch (e) {
        console.error(e);
    }
}

// ==========================================
// View: Review Submission & Listing
// ==========================================
async function loadReviewDoctors() {
    try {
        const res = await fetch(`${API_BASE}/doctors`);
        const json = await res.json();
        if (json.success) {
            const select = document.getElementById('review-doctor-id');
            if (select) {
                select.innerHTML = '<option value="">-- Select Doctor to Review --</option>' +
                    json.data.map(d => `<option value="${d.doctorId}">${d.name} (${d.specialization})</option>`).join('');
            }
        }
        if (state.user && state.user.role === 'PATIENT') {
            document.getElementById('review-patient-id').value = state.user.userId;
        }
    } catch (e) {
        console.error(e);
    }
}

function goToReviewDoctor(docId) {
    navigateTo('review-submit');
    setTimeout(() => {
        document.getElementById('review-doctor-id').value = docId;
    }, 200);
}

async function handleReviewSubmit(e) {
    e.preventDefault();
    const body = {
        doctorId: document.getElementById('review-doctor-id').value,
        patientId: document.getElementById('review-patient-id').value,
        rating: parseInt(document.getElementById('review-rating').value),
        comment: document.getElementById('review-comment').value,
        reviewType: document.getElementById('review-type').value,
        appointmentId: document.getElementById('review-apt-id').value,
        treatmentTag: document.getElementById('review-treatment').value
    };

    try {
        const res = await fetch(`${API_BASE}/reviews`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(body)
        });
        const json = await res.json();
        if (json.success) {
            showToast('Review submitted successfully!', 'success');
            navigateTo('review-list');
        } else {
            showToast(json.message, 'danger');
        }
    } catch (e) {
        showToast('Review submission error: ' + e.message, 'danger');
    }
}

async function loadAllReviews() {
    try {
        const res = await fetch(`${API_BASE}/reviews`);
        const json = await res.json();
        if (json.success) {
            const container = document.getElementById('reviews-list-container');
            if (!container) return;

            if (json.data.length === 0) {
                container.innerHTML = '<div class="text-center text-muted py-5">No reviews yet.</div>';
                return;
            }

            container.innerHTML = json.data.map(r => `
                <div class="card mb-3 shadow-sm border-0">
                    <div class="card-body">
                        <div class="d-flex justify-content-between align-items-center mb-2">
                            <div>
                                <h6 class="mb-0 fw-bold">Doctor: ${r.doctorId}</h6>
                                <small class="text-muted">Reviewed by Patient: ${r.patientId} on ${r.date}</small>
                            </div>
                            <div class="text-end">
                                <span class="badge ${r.reviewType === 'VerifiedReview' ? 'badge-verified' : 'bg-light text-dark'} mb-1">${r.badgeLabel || r.reviewType}</span><br/>
                                <span class="text-warning">${'★'.repeat(r.rating)}${'☆'.repeat(5 - r.rating)}</span>
                            </div>
                        </div>
                        <p class="mb-0 text-secondary">${r.comment}</p>
                    </div>
                </div>
            `).join('');
        }
    } catch (e) {
        console.error(e);
    }
}

// ==========================================
// View: Staff Dashboard & Clinical Operations
// ==========================================
let staffQueueData = [];

async function loadStaffDashboard() {
    if (!state.user || (state.user.role !== 'STAFF' && state.user.role !== 'ADMIN')) {
        navigateTo('login');
        return;
    }

    // Set Staff identity in dashboard header
    const nameEl = document.getElementById('staff-dash-name');
    const idEl = document.getElementById('staff-dash-id');
    const deptEl = document.getElementById('staff-dash-dept');
    if (nameEl) nameEl.textContent = state.user.name || 'Medical Staff';
    if (idEl) idEl.textContent = state.user.userId || 'STF-101';
    if (deptEl) deptEl.textContent = 'Emergency & Clinical Care';

    try {
        const res = await fetch(`${API_BASE}/dashboard/staff-stats`);
        const json = await res.json();
        if (json.success) {
            const data = json.data;
            // Update metric counters
            const schedEl = document.getElementById('staff-stat-scheduled');
            const inprogEl = document.getElementById('staff-stat-inprogress');
            const compEl = document.getElementById('staff-stat-completed');
            const docsEl = document.getElementById('staff-stat-available-docs');

            if (schedEl) schedEl.textContent = data.scheduledCount || 0;
            if (inprogEl) inprogEl.textContent = data.inProgressCount || 0;
            if (compEl) compEl.textContent = data.completedCount || 0;
            if (docsEl) docsEl.textContent = data.availableDoctorsCount || 0;

            // Render Patient Queue
            staffQueueData = data.recentAppointments || [];
            renderStaffAppointmentQueue(staffQueueData);

            // Render Doctor Duty Roster
            renderStaffDoctorRoster(data.doctors || []);
        }
    } catch (e) {
        console.error('Error loading staff dashboard:', e);
        showToast('Error loading staff operational data', 'danger');
    }
}

function renderStaffAppointmentQueue(list) {
    const tbody = document.getElementById('staff-appointment-queue-tbody');
    if (!tbody) return;

    if (!list || list.length === 0) {
        tbody.innerHTML = '<tr><td colspan="7" class="text-center py-4 text-muted">No appointments found in current operational queue.</td></tr>';
        return;
    }

    tbody.innerHTML = list.map(apt => {
        let statusBadge = 'bg-secondary';
        if (apt.status === 'Scheduled') statusBadge = 'bg-primary';
        else if (apt.status === 'In-Progress') statusBadge = 'bg-warning text-dark';
        else if (apt.status === 'Completed') statusBadge = 'bg-success';
        else if (apt.status === 'Cancelled') statusBadge = 'bg-danger';

        return `
            <tr>
                <td><strong>${apt.appointmentId}</strong></td>
                <td><span class="badge bg-light text-dark border">${apt.patientId}</span></td>
                <td>${apt.doctorId}</td>
                <td>${apt.appointmentDate} <span class="text-muted small">${apt.appointmentTime}</span></td>
                <td><span class="badge bg-info text-dark">${apt.appointmentType || 'Standard'}</span></td>
                <td><span class="badge ${statusBadge}">${apt.status}</span></td>
                <td class="text-end">
                    <div class="btn-group btn-group-sm">
                        ${apt.status === 'Scheduled' ? `
                            <button class="btn btn-outline-primary" title="Check-in Patient" onclick="updateStaffAptStatus('${apt.appointmentId}', 'In-Progress')">
                                <i class="bi bi-play-circle me-1"></i>Check In
                            </button>
                        ` : ''}
                        ${(apt.status === 'Scheduled' || apt.status === 'In-Progress') ? `
                            <button class="btn btn-outline-success" title="Mark Completed" onclick="completeStaffApt('${apt.appointmentId}')">
                                <i class="bi bi-check2-circle me-1"></i>Complete
                            </button>
                            <button class="btn btn-outline-danger" title="Cancel Appointment" onclick="cancelStaffApt('${apt.appointmentId}')">
                                <i class="bi bi-x-circle"></i>
                            </button>
                        ` : `
                            <span class="text-muted small py-1 px-2">Archived</span>
                        `}
                    </div>
                </td>
            </tr>
        `;
    }).join('');
}

function filterStaffQueue(keyword) {
    if (!staffQueueData) return;
    if (!keyword || keyword.trim() === '') {
        renderStaffAppointmentQueue(staffQueueData);
        return;
    }
    const q = keyword.toLowerCase().trim();
    const filtered = staffQueueData.filter(a =>
        a.appointmentId.toLowerCase().includes(q) ||
        a.patientId.toLowerCase().includes(q) ||
        a.doctorId.toLowerCase().includes(q) ||
        (a.status && a.status.toLowerCase().includes(q)) ||
        (a.reason && a.reason.toLowerCase().includes(q))
    );
    renderStaffAppointmentQueue(filtered);
}

async function updateStaffAptStatus(aptId, newStatus) {
    try {
        const res = await fetch(`${API_BASE}/appointments/${aptId}`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ status: newStatus })
        });
        const json = await res.json();
        if (json.success) {
            showToast(`Appointment ${aptId} status set to ${newStatus}`, 'success');
            loadStaffDashboard();
        } else {
            showToast(json.message, 'danger');
        }
    } catch (e) {
        showToast('Error updating appointment: ' + e.message, 'danger');
    }
}

async function completeStaffApt(aptId) {
    try {
        const res = await fetch(`${API_BASE}/appointments/${aptId}/complete`, { method: 'PUT' });
        const json = await res.json();
        if (json.success) {
            showToast(`Appointment ${aptId} marked as Completed!`, 'success');
            loadStaffDashboard();
        } else {
            showToast(json.message, 'danger');
        }
    } catch (e) {
        showToast('Error completing appointment: ' + e.message, 'danger');
    }
}

async function cancelStaffApt(aptId) {
    if (!confirm(`Are you sure you want to cancel appointment ${aptId}?`)) return;
    try {
        const res = await fetch(`${API_BASE}/appointments/${aptId}/cancel`, { method: 'PUT' });
        const json = await res.json();
        if (json.success) {
            showToast(`Appointment ${aptId} cancelled`, 'warning');
            loadStaffDashboard();
        } else {
            showToast(json.message, 'danger');
        }
    } catch (e) {
        showToast('Error: ' + e.message, 'danger');
    }
}

function renderStaffDoctorRoster(doctors) {
    const list = document.getElementById('staff-doctor-roster-list');
    if (!list) return;

    if (!doctors || doctors.length === 0) {
        list.innerHTML = '<div class="p-3 text-center text-muted small">No doctor records found.</div>';
        return;
    }

    list.innerHTML = doctors.map(doc => {
        const isAvail = doc.availabilityStatus === 'Available';
        const isBusy = doc.availabilityStatus === 'Busy';
        const badgeClass = isAvail ? 'bg-success' : isBusy ? 'bg-warning text-dark' : 'bg-secondary';

        return `
            <div class="list-group-item d-flex justify-content-between align-items-center py-2 px-3">
                <div>
                    <strong class="d-block text-dark small">${doc.name}</strong>
                    <span class="text-muted" style="font-size: 0.75rem;">${doc.specialization} &bull; ${doc.doctorType || 'Specialist'}</span>
                </div>
                <div class="d-flex align-items-center gap-2">
                    <span class="badge ${badgeClass}" style="font-size: 0.7rem;">${doc.availabilityStatus}</span>
                    <button class="btn btn-sm btn-outline-secondary py-0 px-2" style="font-size: 0.75rem;" 
                            onclick="toggleDoctorDutyStatus('${doc.doctorId}', '${isAvail ? 'Busy' : 'Available'}')">
                        ${isAvail ? 'Mark Busy' : 'Mark Available'}
                    </button>
                </div>
            </div>
        `;
    }).join('');
}

async function toggleDoctorDutyStatus(docId, newStatus) {
    try {
        const getRes = await fetch(`${API_BASE}/doctors/${docId}`);
        const getJson = await getRes.json();
        if (!getJson.success) {
            showToast('Could not fetch doctor info', 'danger');
            return;
        }

        const docData = getJson.data;
        docData.availabilityStatus = newStatus;

        const res = await fetch(`${API_BASE}/doctors/${docId}`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(docData)
        });
        const json = await res.json();
        if (json.success) {
            showToast(`Doctor availability changed to ${newStatus}`, 'info');
            loadStaffDashboard();
        } else {
            showToast(json.message, 'danger');
        }
    } catch (e) {
        showToast('Error updating doctor: ' + e.message, 'danger');
    }
}

// ==========================================
// View: Admin Dashboard & Statistics
// ==========================================
async function loadAdminDashboard() {
    if (!state.user || state.user.role !== 'ADMIN') {
        navigateTo('login');
        return;
    }

    try {
        const res = await fetch(`${API_BASE}/dashboard/admin-stats`);
        const json = await res.json();
        if (json.success) {
            const s = json.data;
            document.getElementById('adm-stat-patients').textContent = s.totalPatients;
            document.getElementById('adm-stat-doctors').textContent = s.totalDoctors;
            document.getElementById('adm-stat-appointments').textContent = s.totalAppointments;
            document.getElementById('adm-stat-pending').textContent = s.pendingAppointments;
            document.getElementById('adm-stat-revenue').textContent = 'LKR ' + s.totalRevenue.toFixed(2);
            document.getElementById('adm-stat-staff').textContent = s.totalStaff;
        }
    } catch (e) {
        console.error(e);
    }
}

// ==========================================
// Admin: Patient Management CRUD
// ==========================================
async function loadAdminPatients(search = '') {
    try {
        let url = `${API_BASE}/patients`;
        if (search) url += `?search=${encodeURIComponent(search)}`;
        const res = await fetch(url);
        const json = await res.json();
        if (json.success) {
            const tbody = document.getElementById('admin-patients-tbody');
            if (!tbody) return;
            tbody.innerHTML = json.data.map(p => `
                <tr>
                    <td><strong>${p.patientId}</strong></td>
                    <td>${p.username}</td>
                    <td>${p.name}</td>
                    <td>${p.email}</td>
                    <td>${p.phone}</td>
                    <td><span class="badge ${p.patientType === 'VIP' ? 'badge-vip' : 'bg-secondary'}">${p.patientType}</span></td>
                    <td>
                        <button class="btn btn-sm btn-outline-primary me-1" onclick='openEditPatientModal(${JSON.stringify(p)})'><i class="bi bi-pencil"></i></button>
                        <button class="btn btn-sm btn-outline-danger" onclick="deletePatientPrompt('${p.patientId}')"><i class="bi bi-trash"></i></button>
                    </td>
                </tr>
            `).join('');
        }
    } catch (e) {
        console.error(e);
    }
}

function openEditPatientModal(p) {
    document.getElementById('admin-edit-patient-id').value = p.patientId;
    document.getElementById('admin-edit-patient-name').value = p.name;
    document.getElementById('admin-edit-patient-email').value = p.email;
    document.getElementById('admin-edit-patient-phone').value = p.phone;
    document.getElementById('admin-edit-patient-address').value = p.address;
    document.getElementById('admin-edit-patient-type').value = p.patientType;
    const modal = new bootstrap.Modal(document.getElementById('adminEditPatientModal'));
    modal.show();
}

async function handleAdminSavePatient(e) {
    e.preventDefault();
    const id = document.getElementById('admin-edit-patient-id').value;
    const body = {
        name: document.getElementById('admin-edit-patient-name').value,
        email: document.getElementById('admin-edit-patient-email').value,
        phone: document.getElementById('admin-edit-patient-phone').value,
        address: document.getElementById('admin-edit-patient-address').value,
        patientType: document.getElementById('admin-edit-patient-type').value
    };

    try {
        const res = await fetch(`${API_BASE}/patients/${id}`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(body)
        });
        const json = await res.json();
        if (json.success) {
            showToast('Patient updated!', 'success');
            bootstrap.Modal.getInstance(document.getElementById('adminEditPatientModal')).hide();
            loadAdminPatients();
        } else {
            showToast(json.message, 'danger');
        }
    } catch (e) {
        showToast('Error: ' + e.message, 'danger');
    }
}

async function deletePatientPrompt(id) {
    if (!confirm(`Are you sure you want to delete patient ${id}?`)) return;
    try {
        const res = await fetch(`${API_BASE}/patients/${id}`, { method: 'DELETE' });
        const json = await res.json();
        if (json.success) {
            showToast('Patient deleted', 'info');
            loadAdminPatients();
        }
    } catch (e) {
        showToast('Error deleting: ' + e.message, 'danger');
    }
}

// ==========================================
// Admin: Doctor Management CRUD
// ==========================================
async function loadAdminDoctors(search = '') {
    try {
        let url = `${API_BASE}/doctors`;
        if (search) url += `?search=${encodeURIComponent(search)}`;
        const res = await fetch(url);
        const json = await res.json();
        if (json.success) {
            const tbody = document.getElementById('admin-doctors-tbody');
            if (!tbody) return;
            tbody.innerHTML = json.data.map(d => `
                <tr>
                    <td><strong>${d.doctorId}</strong></td>
                    <td>${d.name}</td>
                    <td>${d.specialization}</td>
                    <td><span class="badge bg-light text-dark border">${d.doctorType}</span></td>
                    <td>LKR ${d.consultationFee.toFixed(2)}</td>
                    <td><span class="badge ${d.availabilityStatus === 'Available' ? 'bg-success' : 'bg-warning text-dark'}">${d.availabilityStatus}</span></td>
                    <td>
                        <button class="btn btn-sm btn-outline-primary me-1" onclick='openEditDoctorModal(${JSON.stringify(d)})'><i class="bi bi-pencil"></i></button>
                        <button class="btn btn-sm btn-outline-danger" onclick="deleteDoctorPrompt('${d.doctorId}')"><i class="bi bi-trash"></i></button>
                    </td>
                </tr>
            `).join('');
        }
    } catch (e) {
        console.error(e);
    }
}

function openEditDoctorModal(d) {
    document.getElementById('admin-edit-doctor-id').value = d.doctorId;
    document.getElementById('admin-edit-doctor-name').value = d.name;
    document.getElementById('admin-edit-doctor-spec').value = d.specialization;
    document.getElementById('admin-edit-doctor-contact').value = d.contactNumber;
    document.getElementById('admin-edit-doctor-type').value = d.doctorType;
    document.getElementById('admin-edit-doctor-fee').value = d.consultationFee;
    document.getElementById('admin-edit-doctor-status').value = d.availabilityStatus;
    const modal = new bootstrap.Modal(document.getElementById('adminEditDoctorModal'));
    modal.show();
}

async function handleAdminSaveDoctor(e) {
    e.preventDefault();
    const id = document.getElementById('admin-edit-doctor-id').value;
    const body = {
        name: document.getElementById('admin-edit-doctor-name').value,
        specialization: document.getElementById('admin-edit-doctor-spec').value,
        contactNumber: document.getElementById('admin-edit-doctor-contact').value,
        doctorType: document.getElementById('admin-edit-doctor-type').value,
        consultationFee: parseFloat(document.getElementById('admin-edit-doctor-fee').value),
        availabilityStatus: document.getElementById('admin-edit-doctor-status').value
    };

    const isNew = !id;
    const url = isNew ? `${API_BASE}/doctors` : `${API_BASE}/doctors/${id}`;
    const method = isNew ? 'POST' : 'PUT';

    try {
        const res = await fetch(url, {
            method: method,
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(body)
        });
        const json = await res.json();
        if (json.success) {
            showToast('Doctor saved successfully!', 'success');
            bootstrap.Modal.getInstance(document.getElementById('adminEditDoctorModal')).hide();
            loadAdminDoctors();
        } else {
            showToast(json.message, 'danger');
        }
    } catch (e) {
        showToast('Error saving doctor: ' + e.message, 'danger');
    }
}

async function deleteDoctorPrompt(id) {
    if (!confirm(`Are you sure you want to delete doctor ${id}?`)) return;
    try {
        const res = await fetch(`${API_BASE}/doctors/${id}`, { method: 'DELETE' });
        const json = await res.json();
        if (json.success) {
            showToast('Doctor deleted', 'info');
            loadAdminDoctors();
        }
    } catch (e) {
        showToast('Error deleting: ' + e.message, 'danger');
    }
}

// ==========================================
// Admin: Staff Management CRUD
// ==========================================
async function loadAdminStaff(search = '') {
    try {
        let url = `${API_BASE}/staff`;
        if (search) url += `?search=${encodeURIComponent(search)}`;
        const res = await fetch(url);
        const json = await res.json();
        if (json.success) {
            const tbody = document.getElementById('admin-staff-tbody');
            if (!tbody) return;
            tbody.innerHTML = json.data.map(s => `
                <tr>
                    <td><strong>${s.staffId}</strong></td>
                    <td>${s.name}</td>
                    <td><span class="badge bg-info text-dark">${s.position}</span></td>
                    <td>${s.department}</td>
                    <td>${s.contactNumber}</td>
                    <td>LKR ${s.salary.toFixed(2)}</td>
                    <td>
                        <button class="btn btn-sm btn-outline-danger" onclick="deleteStaffPrompt('${s.staffId}')"><i class="bi bi-trash"></i></button>
                    </td>
                </tr>
            `).join('');
        }
    } catch (e) {
        console.error(e);
    }
}

async function handleAdminAddStaff(e) {
    e.preventDefault();
    const body = {
        name: document.getElementById('staff-add-name').value,
        position: document.getElementById('staff-add-pos').value,
        department: document.getElementById('staff-add-dept').value,
        contactNumber: document.getElementById('staff-add-contact').value,
        salary: parseFloat(document.getElementById('staff-add-salary').value)
    };

    try {
        const res = await fetch(`${API_BASE}/staff`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(body)
        });
        const json = await res.json();
        if (json.success) {
            showToast('Staff member added!', 'success');
            document.getElementById('staffAddForm').reset();
            loadAdminStaff();
        } else {
            showToast(json.message, 'danger');
        }
    } catch (e) {
        showToast('Error adding staff: ' + e.message, 'danger');
    }
}

async function deleteStaffPrompt(id) {
    if (!confirm(`Delete staff member ${id}?`)) return;
    try {
        const res = await fetch(`${API_BASE}/staff/${id}`, { method: 'DELETE' });
        const json = await res.json();
        if (json.success) {
            showToast('Staff deleted', 'info');
            loadAdminStaff();
        }
    } catch (e) {
        showToast('Error: ' + e.message, 'danger');
    }
}

// ==========================================
// Admin: Appointment Management CRUD
// ==========================================
async function loadAdminAppointments() {
    try {
        const res = await fetch(`${API_BASE}/appointments`);
        const json = await res.json();
        if (json.success) {
            const tbody = document.getElementById('admin-appointments-tbody');
            if (!tbody) return;
            tbody.innerHTML = json.data.map(a => `
                <tr>
                    <td><strong>${a.appointmentId}</strong></td>
                    <td>${a.patientId}</td>
                    <td>${a.doctorId}</td>
                    <td>${a.appointmentDate} ${a.appointmentTime}</td>
                    <td><span class="badge ${a.appointmentType === 'Emergency' ? 'bg-danger' : 'bg-primary'}">${a.appointmentType}</span></td>
                    <td>
                        <select class="form-select form-select-sm" onchange="updateAppointmentStatus('${a.appointmentId}', this.value)">
                            <option value="Scheduled" ${a.status === 'Scheduled' ? 'selected' : ''}>Scheduled</option>
                            <option value="Completed" ${a.status === 'Completed' ? 'selected' : ''}>Completed</option>
                            <option value="Cancelled" ${a.status === 'Cancelled' ? 'selected' : ''}>Cancelled</option>
                        </select>
                    </td>
                    <td>
                        <button class="btn btn-sm btn-outline-danger" onclick="deleteAppointmentAdminPrompt('${a.appointmentId}')"><i class="bi bi-trash"></i></button>
                    </td>
                </tr>
            `).join('');
        }
    } catch (e) {
        console.error(e);
    }
}

async function updateAppointmentStatus(aptId, newStatus) {
    try {
        const res = await fetch(`${API_BASE}/appointments/${aptId}`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ status: newStatus })
        });
        const json = await res.json();
        if (json.success) {
            showToast(`Appointment ${aptId} updated to ${newStatus}`, 'success');
        } else {
            showToast(json.message, 'danger');
        }
    } catch (e) {
        showToast('Status update error: ' + e.message, 'danger');
    }
}

async function deleteAppointmentAdminPrompt(id) {
    if (!confirm(`Delete appointment record ${id}?`)) return;
    try {
        const res = await fetch(`${API_BASE}/appointments/${id}`, { method: 'DELETE' });
        const json = await res.json();
        if (json.success) {
            showToast('Appointment deleted', 'info');
            loadAdminAppointments();
        }
    } catch (e) {
        showToast('Error: ' + e.message, 'danger');
    }
}

// ==========================================
// Admin: Payment Management CRUD
// ==========================================
async function loadAdminPayments() {
    try {
        const res = await fetch(`${API_BASE}/payments`);
        const json = await res.json();
        if (json.success) {
            const tbody = document.getElementById('admin-payments-tbody');
            if (!tbody) return;
            tbody.innerHTML = json.data.map(p => `
                <tr>
                    <td><strong>${p.paymentId}</strong></td>
                    <td>${p.appointmentId}</td>
                    <td>${p.patientId}</td>
                    <td>LKR ${p.amount.toFixed(2)}</td>
                    <td>${p.paymentMethod}</td>
                    <td>${p.paymentDate}</td>
                    <td><span class="badge ${p.paymentStatus === 'PAID' ? 'bg-success' : 'bg-warning'}">${p.paymentStatus}</span></td>
                    <td>
                        <button class="btn btn-sm btn-outline-danger" onclick="deletePaymentAdminPrompt('${p.paymentId}')"><i class="bi bi-trash"></i></button>
                    </td>
                </tr>
            `).join('');
        }
    } catch (e) {
        console.error(e);
    }
}

async function deletePaymentAdminPrompt(id) {
    if (!confirm(`Delete payment record ${id}?`)) return;
    try {
        const res = await fetch(`${API_BASE}/payments/${id}`, { method: 'DELETE' });
        const json = await res.json();
        if (json.success) {
            showToast('Payment deleted', 'info');
            loadAdminPayments();
        }
    } catch (e) {
        showToast('Error: ' + e.message, 'danger');
    }
}

// ==========================================
// Admin: Review Management CRUD
// ==========================================
async function loadAdminReviews() {
    try {
        const res = await fetch(`${API_BASE}/reviews`);
        const json = await res.json();
        if (json.success) {
            const tbody = document.getElementById('admin-reviews-tbody');
            if (!tbody) return;
            tbody.innerHTML = json.data.map(r => `
                <tr>
                    <td><strong>${r.reviewId}</strong></td>
                    <td>${r.patientId}</td>
                    <td>${r.doctorId}</td>
                    <td><span class="text-warning">${'★'.repeat(r.rating)}</span></td>
                    <td>${r.comment}</td>
                    <td><span class="badge ${r.reviewType === 'VerifiedReview' ? 'badge-verified' : 'bg-light text-dark'}">${r.reviewType}</span></td>
                    <td>
                        <button class="btn btn-sm btn-outline-danger" onclick="deleteReviewAdminPrompt('${r.reviewId}')"><i class="bi bi-trash"></i></button>
                    </td>
                </tr>
            `).join('');
        }
    } catch (e) {
        console.error(e);
    }
}

async function deleteReviewAdminPrompt(id) {
    if (!confirm(`Delete review ${id}?`)) return;
    try {
        const res = await fetch(`${API_BASE}/reviews/${id}`, { method: 'DELETE' });
        const json = await res.json();
        if (json.success) {
            showToast('Review deleted', 'info');
            loadAdminReviews();
        }
    } catch (e) {
        showToast('Error: ' + e.message, 'danger');
    }
}

// ==========================================
// Auth: Login & Registration Handlers
// ==========================================
async function handleLoginFormSubmit(e) {
    e.preventDefault();
    const role = document.getElementById('login-role').value;
    const u = document.getElementById('login-username').value;
    const p = document.getElementById('login-password').value;

    try {
        const res = await fetch(`${API_BASE}/auth/login`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ role: role, username: u, password: p })
        });
        const json = await res.json();
        if (json.success) {
            showToast('Welcome, ' + json.data.name + '!', 'success');
            setSession(json.data);
        } else {
            showToast(json.message, 'danger');
        }
    } catch (e) {
        showToast('Login error: ' + e.message, 'danger');
    }
}

async function handleRegisterFormSubmit(e) {
    e.preventDefault();
    const body = {
        name: document.getElementById('reg-name').value,
        username: document.getElementById('reg-username').value,
        password: document.getElementById('reg-password').value,
        email: document.getElementById('reg-email').value,
        phone: document.getElementById('reg-phone').value,
        address: document.getElementById('reg-address').value,
        patientType: document.getElementById('reg-type').value
    };

    try {
        const res = await fetch(`${API_BASE}/patients/register`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(body)
        });
        const json = await res.json();
        if (json.success) {
            showToast('Registration successful! Please login.', 'success');
            navigateTo('login');
            setTimeout(() => {
                document.getElementById('login-username').value = body.username;
            }, 200);
        } else {
            showToast(json.message, 'danger');
        }
    } catch (e) {
        showToast('Registration error: ' + e.message, 'danger');
    }
}

// ==========================================
// Event Listeners Setup
// ==========================================
function setupEventListeners() {
    // Form submits
    const regForm = document.getElementById('patientRegistrationForm');
    if (regForm) regForm.addEventListener('submit', handleRegisterFormSubmit);

    const loginForm = document.getElementById('patientLoginForm');
    if (loginForm) loginForm.addEventListener('submit', handleLoginFormSubmit);

    const profForm = document.getElementById('patientProfileForm');
    if (profForm) profForm.addEventListener('submit', handleProfileUpdate);

    const bookForm = document.getElementById('appointmentBookingForm');
    if (bookForm) bookForm.addEventListener('submit', handleAppointmentBooking);

    const payForm = document.getElementById('paymentProcessingForm');
    if (payForm) payForm.addEventListener('submit', handlePaymentSubmit);

    const revForm = document.getElementById('reviewSubmissionForm');
    if (revForm) revForm.addEventListener('submit', handleReviewSubmit);

    const reschedForm = document.getElementById('rescheduleForm');
    if (reschedForm) reschedForm.addEventListener('submit', handleAppointmentRescheduleSubmit);

    const adminEditPatientForm = document.getElementById('adminEditPatientForm');
    if (adminEditPatientForm) adminEditPatientForm.addEventListener('submit', handleAdminSavePatient);

    const adminEditDoctorForm = document.getElementById('adminEditDoctorForm');
    if (adminEditDoctorForm) adminEditDoctorForm.addEventListener('submit', handleAdminSaveDoctor);

    const staffAddForm = document.getElementById('staffAddForm');
    if (staffAddForm) staffAddForm.addEventListener('submit', handleAdminAddStaff);

    // Doctor filter triggers
    const specFilter = document.getElementById('filter-doctor-spec');
    if (specFilter) specFilter.addEventListener('change', () => loadDoctorsList(specFilter.value, '', ''));

    const searchDocInput = document.getElementById('search-doctor-input');
    if (searchDocInput) searchDocInput.addEventListener('input', (e) => loadDoctorsList('', '', e.target.value));

    // Availability checker button
    const checkBtn = document.getElementById('btn-check-availability');
    if (checkBtn) checkBtn.addEventListener('click', checkBookingAvailability);

    // Payment appointment change listener
    const payAptSelect = document.getElementById('pay-appointment-id');
    if (payAptSelect) payAptSelect.addEventListener('change', (e) => updatePaymentAmountPreview(e.target.value));
}

// ==========================================
// Toast Helper
// ==========================================
function showToast(message, type = 'info') {
    const container = document.getElementById('toast-container');
    if (!container) return;

    const toastEl = document.createElement('div');
    toastEl.className = `toast align-items-center text-white bg-${type === 'danger' ? 'danger' : type === 'success' ? 'success' : type === 'warning' ? 'warning text-dark' : 'primary'} border-0 mb-2`;
    toastEl.setAttribute('role', 'alert');
    toastEl.setAttribute('aria-live', 'assertive');
    toastEl.setAttribute('aria-atomic', 'true');

    toastEl.innerHTML = `
        <div class="d-flex">
            <div class="toast-body">
                <i class="bi ${type === 'success' ? 'bi-check-circle' : type === 'danger' ? 'bi-exclamation-triangle' : 'bi-info-circle'} me-2"></i>
                ${message}
            </div>
            <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast" aria-label="Close"></button>
        </div>
    `;

    container.appendChild(toastEl);
    const toast = new bootstrap.Toast(toastEl, { delay: 4000 });
    toast.show();
    toastEl.addEventListener('hidden.bs.toast', () => toastEl.remove());
}

// ==========================================
// Modal & Utility Helpers
// ==========================================
function openNewDoctorModal() {
    document.getElementById('admin-edit-doctor-id').value = '';
    document.getElementById('admin-edit-doctor-name').value = 'Dr. ';
    document.getElementById('admin-edit-doctor-spec').value = 'Cardiology';
    document.getElementById('admin-edit-doctor-contact').value = '0771234567';
    document.getElementById('admin-edit-doctor-fee').value = '3500';
    document.getElementById('admin-edit-doctor-type').value = 'SpecialistDoctor';
    document.getElementById('admin-edit-doctor-status').value = 'Available';
    document.getElementById('admin-doc-modal-title').textContent = 'Add New Doctor';
    new bootstrap.Modal(document.getElementById('adminEditDoctorModal')).show();
}

function togglePaymentInputs(method) {
    const label = document.getElementById('pay-detail-label');
    const input = document.getElementById('pay-detail-input');
    if (!label || !input) return;
    if (method === 'CARD') {
        label.textContent = 'Card Number (Mock)';
        input.value = '4532882199004242';
        input.placeholder = '4242 •••• •••• 4242';
    } else if (method === 'ONLINE') {
        label.textContent = 'Online Gateway Service';
        input.value = 'PayHere Secure Gateway';
        input.placeholder = 'Gateway name or wallet ref';
    } else {
        label.textContent = 'Cash Desk Notes';
        input.value = 'Hospital Front Desk Counter #1';
        input.placeholder = 'Counter receipt note';
    }
}

// Global window exposure for inline onclick attributes
window.quickSwitchRole = quickSwitchRole;
window.openNewDoctorModal = openNewDoctorModal;
window.togglePaymentInputs = togglePaymentInputs;
window.loadDoctorsList = loadDoctorsList;
window.loadAdminPatients = loadAdminPatients;
window.loadAdminDoctors = loadAdminDoctors;
window.loadAdminStaff = loadAdminStaff;
window.loadAdminAppointments = loadAdminAppointments;
window.loadAdminPayments = loadAdminPayments;
window.loadAdminReviews = loadAdminReviews;
window.openRescheduleModal = openRescheduleModal;
window.cancelAppointmentPrompt = cancelAppointmentPrompt;
window.goToPaymentForAppointment = goToPaymentForAppointment;
window.goToReviewDoctor = goToReviewDoctor;
window.viewReceiptModal = viewReceiptModal;
window.deletePatientPrompt = deletePatientPrompt;
window.openEditPatientModal = openEditPatientModal;
window.deleteDoctorPrompt = deleteDoctorPrompt;
window.openEditDoctorModal = openEditDoctorModal;
window.deleteStaffPrompt = deleteStaffPrompt;
window.deleteAppointmentPrompt = deleteAppointmentPrompt;
window.updateAdminAppointmentStatus = updateAdminAppointmentStatus;
window.deleteReviewAdminPrompt = deleteReviewAdminPrompt;
window.filterStaffQueue = filterStaffQueue;
window.updateStaffAptStatus = updateStaffAptStatus;
window.completeStaffApt = completeStaffApt;
window.cancelStaffApt = cancelStaffApt;
window.toggleDoctorDutyStatus = toggleDoctorDutyStatus;

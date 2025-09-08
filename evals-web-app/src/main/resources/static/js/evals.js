// JavaScript utilities for Evals Manager
console.log('Evals Manager loaded');

// Common utilities that could be shared across pages
window.EvalUtils = {
    // Show loading state on buttons
    setButtonLoading: function(button, loading = true) {
        if (loading) {
            button.disabled = true;
            const originalText = button.innerHTML;
            button.setAttribute('data-original-text', originalText);
            button.innerHTML = '<span class="spinner-border spinner-border-sm me-2" role="status"></span>Loading...';
        } else {
            button.disabled = false;
            const originalText = button.getAttribute('data-original-text');
            if (originalText) {
                button.innerHTML = originalText;
                button.removeAttribute('data-original-text');
            }
        }
    },

    // Format timestamps consistently
    formatTimestamp: function(timestamp) {
        return new Date(timestamp).toLocaleString();
    },

    // Confirm dialog utility
    confirm: function(message, callback) {
        if (confirm(message)) {
            callback();
        }
    }
};

// Auto-hide alerts after 5 seconds
document.addEventListener('DOMContentLoaded', function() {
    const alerts = document.querySelectorAll('.alert-dismissible');
    alerts.forEach(function(alert) {
        setTimeout(function() {
            const bsAlert = new bootstrap.Alert(alert);
            bsAlert.close();
        }, 5000);
    });
});

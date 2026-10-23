/**
 * Main Client Scripts
 */
document.addEventListener('DOMContentLoaded', () => {
  // Handle User Dropdown Toggle on Click
  const dropdownWraps = document.querySelectorAll('.user-dropdown-wrap');

  dropdownWraps.forEach(wrap => {
    const trigger = wrap.querySelector('.user-dropdown-btn');
    if (!trigger) return;

    trigger.addEventListener('click', (e) => {
      e.preventDefault();
      e.stopPropagation();
      const isActive = wrap.classList.contains('active');
      
      // Close any other open dropdowns
      dropdownWraps.forEach(w => {
        w.classList.remove('active');
        const btn = w.querySelector('.user-dropdown-btn');
        if (btn) btn.setAttribute('aria-expanded', 'false');
      });

      if (!isActive) {
        wrap.classList.add('active');
        trigger.setAttribute('aria-expanded', 'true');
      }
    });
  });

  // Close dropdown when clicking outside
  document.addEventListener('click', (e) => {
    dropdownWraps.forEach(wrap => {
      if (!wrap.contains(e.target)) {
        wrap.classList.remove('active');
        const btn = wrap.querySelector('.user-dropdown-btn');
        if (btn) btn.setAttribute('aria-expanded', 'false');
      }
    });
  });

  // Close dropdown when pressing Escape key
  document.addEventListener('keydown', (e) => {
    if (e.key === 'Escape') {
      dropdownWraps.forEach(wrap => {
        wrap.classList.remove('active');
        const btn = wrap.querySelector('.user-dropdown-btn');
        if (btn) btn.setAttribute('aria-expanded', 'false');
      });
    }
  });
});

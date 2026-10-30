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

  // Universal header search: press Enter to search products on non-product pages
  const searchInputs = document.querySelectorAll('.search-pill-box input');
  searchInputs.forEach(input => {
    input.addEventListener('keypress', (e) => {
      if (e.key === 'Enter') {
        // If on product page, product.html handles its own filtering
        if (document.getElementById('productGrid')) {
          return;
        }
        e.preventDefault();
        const val = input.value.trim();
        window.location.href = '/products' + (val ? ('?keyword=' + encodeURIComponent(val)) : '');
      }
    });
  });
});

/**
 * Client Helper JS (SSR Progressive Enhancement Style)
 * - Toast Notification System
 * - Modal Open / Close Handler
 * - URL Query Param Navigation (Search, Filter, Sort, Pagination)
 * - Quick Actions via Fetch API (Cart Quantity Stepper, Item Removal, Add To Cart)
 */

(function () {
  'use strict';

  // Format currency in Vietnamese Dong
  function formatVND(value) {
    if (value === null || value === undefined) return '0₫';
    const num = typeof value === 'number' ? value : parseFloat(value);
    if (isNaN(num)) return value;
    return new Intl.NumberFormat('vi-VN').format(num) + '₫';
  }

  // ==========================================
  // 1. TOAST NOTIFICATION SYSTEM
  // ==========================================
  function showToast(message, type = 'success') {
    let container = document.querySelector('.toast-container');
    if (!container) {
      container = document.createElement('div');
      container.className = 'toast-container';
      document.body.appendChild(container);
    }

    const toast = document.createElement('div');
    toast.className = 'nike-toast';

    const iconSvg = type === 'success'
      ? `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor"><polyline points="20 6 9 17 4 12"></polyline></svg>`
      : `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor"><circle cx="12" cy="12" r="10"></circle><line x1="12" y1="8" x2="12" y2="12"></line><line x1="12" y1="16" x2="12.01" y2="16"></line></svg>`;

    toast.innerHTML = `
      <div class="nike-toast-icon">${iconSvg}</div>
      <div class="nike-toast-msg">${message}</div>
    `;

    container.appendChild(toast);

    // Trigger animation
    requestAnimationFrame(() => {
      toast.classList.add('show');
    });

    // Auto dismiss after 3 seconds
    setTimeout(() => {
      toast.classList.remove('show');
      setTimeout(() => {
        if (toast.parentElement) {
          toast.parentElement.removeChild(toast);
        }
      }, 350);
    }, 3000);
  }

  // ==========================================
  // 2. MODAL HELPER (POPUP CHỌN SIZE, XEM NHANH)
  // ==========================================
  function openModal(modalId) {
    const modal = document.getElementById(modalId);
    if (!modal) return;
    modal.classList.add('active');
    document.body.style.overflow = 'hidden';
  }

  function closeModal(modalId) {
    const modal = document.getElementById(modalId);
    if (!modal) return;
    modal.classList.remove('active');
    document.body.style.overflow = '';
  }

  // ==========================================
  // 3. URL QUERY PARAM NAVIGATION
  // Bắt sự kiện filter / search / sort -> Cập nhật URL & window.location.href
  // để Controller & Thymeleaf tải lại trang (SSR)
  // ==========================================
  function updateQueryParam(key, value, resetPage = true) {
    const url = new URL(window.location.href);
    if (value === null || value === undefined || value === '' || value === 'ALL') {
      url.searchParams.delete(key);
    } else {
      url.searchParams.set(key, value);
    }
    if (resetPage) {
      url.searchParams.delete('page');
    }
    window.location.href = url.toString();
  }

  function initUrlNavigation() {
    // Search input (Enter key)
    const searchInputs = document.querySelectorAll('.search-pill-box input');
    searchInputs.forEach(input => {
      input.addEventListener('keydown', (e) => {
        if (e.key === 'Enter') {
          const query = input.value.trim();
          if (query) {
            window.location.href = '/products?search=' + encodeURIComponent(query);
          }
        }
      });
    });

    // Sort / Filter select changes
    const filterSelects = document.querySelectorAll('select[data-filter-key]');
    filterSelects.forEach(select => {
      select.addEventListener('change', () => {
        const key = select.getAttribute('data-filter-key');
        updateQueryParam(key, select.value);
      });
    });
  }

  // ==========================================
  // 4. QUICK ACTIONS VIA FETCH API (CART & DOM SYNC)
  // ==========================================

  // Update header cart badge
  function updateHeaderBadge(totalQuantity) {
    const badges = document.querySelectorAll('.cart-count-badge');
    badges.forEach(badge => {
      badge.textContent = totalQuantity || 0;
    });
  }

  // Update summary sidebar UI on cart page
  function updateCartSummary(cartData) {
    if (!cartData) return;

    const subtotalEl = document.getElementById('summary-subtotal');
    if (subtotalEl) {
      subtotalEl.textContent = formatVND(cartData.subtotal);
    }

    const discountEl = document.getElementById('summary-discount');
    if (discountEl) {
      discountEl.textContent = (cartData.discount && cartData.discount > 0)
        ? '-' + formatVND(cartData.discount)
        : '0₫';
    }

    const totalEl = document.getElementById('summary-total');
    if (totalEl) {
      totalEl.textContent = formatVND(cartData.total);
    }

    const countLabel = document.getElementById('summary-count-label');
    const itemsCount = cartData.items ? cartData.items.length : 0;
    if (countLabel) {
      countLabel.textContent = `Tạm tính (${itemsCount} sản phẩm)`;
    }

    const totalCountHeader = document.getElementById('cart-total-count');
    if (totalCountHeader) {
      totalCountHeader.textContent = itemsCount;
    }

    // Check if cart is empty
    if (itemsCount === 0) {
      const itemsSection = document.getElementById('cart-items-section');
      if (itemsSection) {
        let emptyState = document.getElementById('cart-empty-message');
        if (!emptyState) {
          emptyState = document.createElement('div');
          emptyState.className = 'cart-empty-state';
          emptyState.id = 'cart-empty-message';
          emptyState.innerHTML = `
            <div class="cart-empty-title">Giỏ hàng của bạn đang trống</div>
            <p class="cart-empty-desc">Hãy khám phá bộ sưu tập mới nhất từ Nike và chọn cho mình sản phẩm ưng ý.</p>
            <a href="/products" class="btn-primary" style="display:inline-flex; align-items:center; height:44px; padding:0 24px;">Khám Phá Sản Phẩm</a>
          `;
          itemsSection.appendChild(emptyState);
        } else {
          emptyState.style.display = 'block';
        }
      }
    }
  }

  // API Call: Update item quantity
  async function apiUpdateQuantity(itemId, newQty) {
    try {
      const res = await fetch(`/api/cart/${itemId}?quantity=${newQty}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' }
      });
      if (!res.ok) throw new Error('Cập nhật thất bại');
      return await res.json();
    } catch (err) {
      console.error(err);
      showToast('Có lỗi xảy ra khi cập nhật số lượng!', 'error');
      return null;
    }
  }

  // API Call: Remove item from cart
  async function apiRemoveItem(itemId) {
    try {
      const res = await fetch(`/api/cart/${itemId}`, {
        method: 'DELETE'
      });
      if (!res.ok) throw new Error('Xóa thất bại');
      return await res.json();
    } catch (err) {
      console.error(err);
      showToast('Có lỗi xảy ra khi xóa sản phẩm!', 'error');
      return null;
    }
  }

  // API Call: Add to cart
  async function apiAddToCart(productId, sizeId = null, quantity = 1) {
    try {
      let url = `/api/cart?productId=${productId}&quantity=${quantity}`;
      if (sizeId) url += `&sizeId=${sizeId}`;

      const res = await fetch(url, { method: 'POST' });
      if (!res.ok) throw new Error('Thêm vào giỏ thất bại');
      const cartData = await res.json();
      updateHeaderBadge(cartData.totalQuantity);
      showToast('✓ Đã thêm sản phẩm vào giỏ hàng!', 'success');
      return cartData;
    } catch (err) {
      console.error(err);
      showToast('Không thể thêm sản phẩm vào giỏ hàng!', 'error');
      return null;
    }
  }

  // Setup Cart Page Interactive Quick Actions
  function initCartQuickActions() {
    const itemsSection = document.getElementById('cart-items-section');
    if (!itemsSection) return;

    // Handle Minus Button
    itemsSection.addEventListener('click', async (e) => {
      const minusBtn = e.target.closest('.btn-minus');
      if (minusBtn) {
        e.preventDefault();
        const id = minusBtn.getAttribute('data-id');
        const input = minusBtn.parentElement.querySelector('.qty-input');
        if (!input) return;

        let currentVal = parseInt(input.value, 10) || 1;
        if (currentVal > 1) {
          minusBtn.disabled = true;
          const updatedCart = await apiUpdateQuantity(id, currentVal - 1);
          minusBtn.disabled = false;

          if (updatedCart) {
            input.value = currentVal - 1;
            // Update line item price
            const item = updatedCart.items.find(it => String(it.id) === String(id));
            if (item) {
              const priceEl = document.getElementById(`cart-item-price-${id}`);
              if (priceEl) {
                const hasDiscount = item.salePrice && item.salePrice > 0 && item.salePrice < item.price;
                if (hasDiscount) {
                  priceEl.innerHTML = `
                    <div style="color:var(--color-sale);">${formatVND(item.itemTotal)}</div>
                    <div class="price-original" style="font-size:14px; color:var(--color-mute); text-decoration:line-through;">${formatVND(item.price * item.quantity)}</div>
                  `;
                } else {
                  priceEl.innerHTML = `<div>${formatVND(item.itemTotal)}</div>`;
                }
              }
            }
            updateCartSummary(updatedCart);
            updateHeaderBadge(updatedCart.totalQuantity);
            showToast('Đã giảm số lượng sản phẩm');
          }
        } else {
          // If qty is 1, prompt to confirm deletion
          if (window.confirm('Bạn có muốn xóa sản phẩm này khỏi giỏ hàng không?')) {
            minusBtn.disabled = true;
            const updatedCart = await apiRemoveItem(id);
            if (updatedCart) {
              const itemCard = document.getElementById(`cart-item-${id}`);
              if (itemCard) {
                itemCard.style.opacity = '0';
                itemCard.style.transform = 'translateX(20px)';
                setTimeout(() => itemCard.remove(), 250);
              }
              updateCartSummary(updatedCart);
              updateHeaderBadge(updatedCart.totalQuantity);
              showToast('Đã xóa sản phẩm khỏi giỏ hàng');
            }
          }
        }
        return;
      }

      // Handle Plus Button
      const plusBtn = e.target.closest('.btn-plus');
      if (plusBtn) {
        e.preventDefault();
        const id = plusBtn.getAttribute('data-id');
        const input = plusBtn.parentElement.querySelector('.qty-input');
        if (!input) return;

        let currentVal = parseInt(input.value, 10) || 1;
        if (currentVal < 99) {
          plusBtn.disabled = true;
          const updatedCart = await apiUpdateQuantity(id, currentVal + 1);
          plusBtn.disabled = false;

          if (updatedCart) {
            input.value = currentVal + 1;
            const item = updatedCart.items.find(it => String(it.id) === String(id));
            if (item) {
              const priceEl = document.getElementById(`cart-item-price-${id}`);
              if (priceEl) {
                const hasDiscount = item.salePrice && item.salePrice > 0 && item.salePrice < item.price;
                if (hasDiscount) {
                  priceEl.innerHTML = `
                    <div style="color:var(--color-sale);">${formatVND(item.itemTotal)}</div>
                    <div class="price-original" style="font-size:14px; color:var(--color-mute); text-decoration:line-through;">${formatVND(item.price * item.quantity)}</div>
                  `;
                } else {
                  priceEl.innerHTML = `<div>${formatVND(item.itemTotal)}</div>`;
                }
              }
            }
            updateCartSummary(updatedCart);
            updateHeaderBadge(updatedCart.totalQuantity);
            showToast('Đã tăng số lượng sản phẩm');
          }
        }
        return;
      }

      // Handle Remove Item Button
      const removeBtn = e.target.closest('.btn-remove-item');
      if (removeBtn) {
        e.preventDefault();
        const id = removeBtn.getAttribute('data-id');
        removeBtn.disabled = true;
        removeBtn.textContent = 'Đang xóa...';

        const updatedCart = await apiRemoveItem(id);
        if (updatedCart) {
          const itemCard = document.getElementById(`cart-item-${id}`);
          if (itemCard) {
            itemCard.style.opacity = '0';
            itemCard.style.transform = 'translateX(20px)';
            setTimeout(() => itemCard.remove(), 250);
          }
          updateCartSummary(updatedCart);
          updateHeaderBadge(updatedCart.totalQuantity);
          showToast('Đã xóa sản phẩm khỏi giỏ hàng');
        } else {
          removeBtn.disabled = false;
          removeBtn.textContent = 'Xóa khỏi giỏ';
        }
        return;
      }

      // Handle Favorite Button
      const favBtn = e.target.closest('.btn-save-favorite');
      if (favBtn) {
        e.preventDefault();
        favBtn.textContent = '✓ Đã lưu yêu thích';
        favBtn.style.color = 'var(--color-success, #007a3d)';
        showToast('Đã lưu vào danh sách yêu thích!');
      }
    });
  }

  // Page Load Initialization
  document.addEventListener('DOMContentLoaded', () => {
    initUrlNavigation();
    initCartQuickActions();

    // Setup global Add to Cart click delegation (e.g. from PDP or product card)
    document.addEventListener('click', (e) => {
      const btn = e.target.closest('[data-add-to-cart]');
      if (btn) {
        e.preventDefault();
        const productId = btn.getAttribute('data-add-to-cart');
        const sizeId = btn.getAttribute('data-size-id') || null;
        const qty = parseInt(btn.getAttribute('data-quantity'), 10) || 1;
        apiAddToCart(productId, sizeId, qty);
      }
    });
  });

  // Expose Global Helper API
  window.NikeApp = {
    showToast,
    openModal,
    closeModal,
    updateQueryParam,
    apiAddToCart,
    apiUpdateQuantity,
    apiRemoveItem,
    formatVND
  };

  // Backward compatibility
  window.NikeClientAPI = window.NikeApp;

})();

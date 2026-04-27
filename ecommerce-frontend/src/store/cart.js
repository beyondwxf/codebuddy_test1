import { defineStore } from 'pinia'
import { listCart, addToCart, updateCart, deleteCart } from '@/api/cart'

export const useCartStore = defineStore('cart', {
  state: () => ({
    cartList: [],
    selectedIds: []
  }),
  getters: {
    cartCount: (state) => state.cartList.reduce((sum, item) => sum + item.quantity, 0),
    selectedItems: (state) => state.cartList.filter(item => state.selectedIds.includes(item.cartId)),
    totalAmount: (state) => {
      return state.cartList
        .filter(item => state.selectedIds.includes(item.cartId))
        .reduce((sum, item) => sum + item.salePrice * item.quantity, 0)
    }
  },
  actions: {
    async fetchCart() {
      const res = await listCart()
      this.cartList = res.data || []
    },
    async addItem(productId, quantity = 1) {
      await addToCart({ productId, quantity })
      await this.fetchCart()
    },
    async updateItem(cartId, quantity) {
      await updateCart({ cartId, quantity })
      await this.fetchCart()
    },
    async removeItems(cartIds) {
      await deleteCart(cartIds)
      this.selectedIds = this.selectedIds.filter(id => !cartIds.toString().split(',').map(Number).includes(id))
      await this.fetchCart()
    },
    toggleSelect(cartId) {
      const idx = this.selectedIds.indexOf(cartId)
      if (idx > -1) {
        this.selectedIds.splice(idx, 1)
      } else {
        this.selectedIds.push(cartId)
      }
    },
    toggleSelectAll() {
      if (this.selectedIds.length === this.cartList.length) {
        this.selectedIds = []
      } else {
        this.selectedIds = this.cartList.map(item => item.cartId)
      }
    }
  }
})

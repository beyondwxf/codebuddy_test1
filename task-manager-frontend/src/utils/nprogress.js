// NProgress 样式（内联避免额外依赖）
const NProgress = {
  start() { document.body.classList.add('loading') },
  done() { document.body.classList.remove('loading') }
}
window.NProgress = NProgress

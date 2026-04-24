import hasPermi from './permission/hasPermi'

const install = (app) => {
  app.directive('hasPermi', hasPermi)
}

export default install

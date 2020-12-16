import Vue from 'vue'

const maskCellphone = Vue.filter('maskCellphone', (value) => {
  return value.toString().replace(/^(\d{2})(\d{5})(\d{4}).*/, '($1) $2-$3')
})

export default {
  maskCellphone,
}

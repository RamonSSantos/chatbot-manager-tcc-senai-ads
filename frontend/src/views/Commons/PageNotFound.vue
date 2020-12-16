<template>
  <div>
    <h1 class="text-h6 grey--text">
      A página que você está procurando não foi encontrada!
    </h1>
    <router-link :to="{ name: 'Dashboard' }">
      Voltar para a página inicial
    </router-link>
  </div>
</template>

<script>
export default {
  name: 'PageNotFound',
}
</script>

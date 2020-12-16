<template>
  <div>
    <h1 class="text-h6 grey--text">
      O site está fora do ar. Entre em contato com o administrador!
    </h1>
    <router-link :to="{ name: 'Dashboard' }">
      Voltar para a página inicial
    </router-link>
  </div>
</template>

<script>
export default {
  name: 'NetworkIssue',
}
</script>

<template>
  <v-footer color="blue darken-2" absolute dark>
    <v-row justify="center">
      <div>&copy; {{ new Date().getFullYear() }}</div>
    </v-row>
  </v-footer>
</template>

<script>
export default {
  name: 'Footer',
}
</script>

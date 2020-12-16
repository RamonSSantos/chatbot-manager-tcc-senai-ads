<template>
  <v-snackbar
    v-model="snackbar"
    :color="notification.type"
    :value="true"
    :light="notification.isLight"
    bottom
    right
  >
    <v-icon class="mr-2">{{ notification.icon }}</v-icon>
    {{ notification.message }}
    <template v-slot:action="{ attrs }">
      <v-btn v-bind="attrs" @click.stop="snackbar = false" icon>
        <v-icon>mdi-close</v-icon>
      </v-btn>
    </template>
  </v-snackbar>
</template>

<script>
import { mapActions } from 'vuex'

export default {
  name: 'NotificationBar',
  props: {
    notification: {
      type: Object,
      required: true,
    },
  },
  computed: {
    notificationType() {
      return this.notification.type
    },
  },
  data: () => ({
    snackbar: false,
    timeout: null,
  }),
  created() {
    this.snackbar = true
  },
  mounted() {
    this.timeout = setTimeout(() => this.remove(this.notification), 3000)
  },
  beforeDestroy() {
    this.snackbar = false
    clearTimeout(this.timeout)
  },
  methods: mapActions('notification', ['remove']),
}
</script>

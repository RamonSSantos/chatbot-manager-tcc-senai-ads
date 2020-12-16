<template>
  <v-app>
    <template
      v-if="
        this.$route.name === 'Login' ||
        this.$route.name === 'ResetPassword' ||
        this.$route.name === null
      "
    >
      <NotificationContainer />
      <router-view />
    </template>
    <template
      v-else-if="
        this.$route.name !== 'Login' ||
        this.$route.name !== 'ResetPassword' ||
        this.$route.name !== null
      "
    >
      <Navbar />
      <v-main id="main" class="px-4 pb-4">
        <NotificationContainer />
        <router-view />
      </v-main>
      <!--<Footer />-->
    </template>
  </v-app>
</template>

<script>
import Navbar from '@/components/Navbar'
import NotificationContainer from '@/components/notification/NotificationContainer'
/*import Footer from '@/components/Footer'*/

export default {
  name: 'App',
  components: { Navbar, NotificationContainer },
}
</script>

<style>
#nprogress .bar {
  height: 0.2rem !important;
  background: #f57c00 !important;
  z-index: 0 !important;
}

#nprogress .spinner-icon {
  border-top-color: #f57c00 !important;
  border-left-color: #f57c00 !important;
}

.v-dialog:not(.v-dialog--fullscreen) {
  max-height: 100%;
}

.v-breadcrumbs li:nth-child(2n) {
  padding: 0;
}

.v-application--is-ltr .v-stepper__label {
  text-align: center;
}

.v-stepper__label {
  align-items: center;
}
</style>

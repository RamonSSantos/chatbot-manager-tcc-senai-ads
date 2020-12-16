<template>
  <div class="d-inline-flex">
    <v-breadcrumbs class="pr-0">
      <v-breadcrumbs-item :to="{ name: 'Dashboard' }">
        HOME
      </v-breadcrumbs-item>
      <v-icon class="font-size-icon px-1">mdi-chevron-right</v-icon>
    </v-breadcrumbs>
    <v-breadcrumbs :items="items" class="px-0">
      <template v-slot:item="{ item }">
        <ul class="pl-0">
          <li class="px-0">
            <a @click.stop="goToParentPage(item.to)">
              {{ item.text.toUpperCase() }}
            </a>
          </li>
        </ul>
      </template>
      <template v-slot:divider>
        <v-icon class="px-1">mdi-chevron-right</v-icon>
      </template>
    </v-breadcrumbs>
  </div>
</template>

<script>
import EventBus from '@/services/EventBus'

export default {
  name: 'Breadcrumb',
  computed: {
    items() {
      let pathArray = this.$route.path.split('/')
      pathArray.shift()
      if (pathArray.length === 3) pathArray.pop()

      let breadcrumbs = pathArray.reduce((breadcrumbArray, path, idx) => {
        breadcrumbArray.push({
          path: path,
          to: breadcrumbArray[idx - 1]
            ? `/${breadcrumbArray[idx - 1].path}/${path}`
            : {
                name: this.$route.matched[idx].name,
              },
          text: this.$route.matched[idx].meta.breadcrumb || path,
        })
        return breadcrumbArray
      }, [])
      return breadcrumbs
    },
  },
  methods: {
    goToParentPage(name) {
      if (name.name === 'Contents') {
        EventBus.$emit('contents', false)
      }
      this.$router.push(name)
    },
  },
}
</script>

<style scoped>
.font-size-icon {
  font-size: 14px;
}
</style>

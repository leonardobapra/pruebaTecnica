<template>
  <!-- Mobile App Bar (shown only on mobile) -->
  <v-app-bar flat class="mobile-app-bar d-md-none" height="0">
    <v-app-bar-nav-icon color="white" @click="drawer = !drawer"></v-app-bar-nav-icon>
    <div class="d-flex align-center ml-2">
      <v-icon icon="mdi-heart-flash" color="cyan" size="24" class="logo-icon mr-2"></v-icon>
      <span class="text-white font-weight-bold text-subtitle-1">GoEcosystem</span>
    </div>
  </v-app-bar>

  <!-- Sidebar Navigation Drawer (Left Side) -->
  <v-navigation-drawer
    v-model="drawer"
    :temporary="!$vuetify.display.mdAndUp"
    class="sidebar-drawer"
    width="280"
    theme="dark"
  >
    <!-- Logo Header -->
    <div class="brand-container pa-6 d-flex align-center">
      <v-icon icon="mdi-heart-flash" color="cyan" size="36" class="logo-icon mr-3"></v-icon>
      <div class="brand-text">
        <span class="text-white font-weight-bold text-h6">GoEcosystem</span>
        <span class="text-cyan font-weight-semibold text-caption d-block mt-n1"
          >DIGITAL HEALTH</span
        >
      </div>
    </div>

    <v-divider class="mx-4 mb-4"></v-divider>

    <!-- Navigation List -->
    <v-list class="px-3" density="comfortable" nav>
      <v-list-item
        prepend-icon="mdi-account-multiple"
        title="Lista de Pacientes"
        :active="isActiveRoute('/')"
        active-color="cyan"
        class="nav-item mb-2"
        @click="goToPatients"
      ></v-list-item>

      <v-list-item
        prepend-icon="mdi-view-dashboard"
        title="Dashboard"
        :active="isActiveRoute('/dashboard-page')"
        active-color="cyan"
        class="nav-item mb-2"
        @click="goToDashboard"
      ></v-list-item>
    </v-list>

    <!-- Append Section (User Info & Logout) -->
    <template v-slot:append>
      <v-divider class="mx-4 my-2"></v-divider>
      <div class="user-profile-section pa-4">
        <div class="d-flex align-center mb-4">
          <v-avatar color="cyan-darken-3" size="40" class="mr-3 border-cyan">
            <span class="text-white font-weight-bold">AD</span>
          </v-avatar>
          <div class="user-details overflow-hidden">
            <span class="text-subtitle-2 font-weight-bold text-white d-block text-truncate">
              Administrador Demo
            </span>
            <v-chip color="red" size="x-small" variant="flat" class="font-weight-bold mt-1 px-2">
              ADMIN
            </v-chip>
          </div>
        </div>

        <v-btn
          color="red-lighten-1"
          variant="tonal"
          block
          prepend-icon="mdi-logout"
          class="text-none font-weight-bold logout-btn"
          @click="logout"
        >
          Cerrar Sesión
        </v-btn>
      </div>
    </template>
  </v-navigation-drawer>
</template>

<script>
export default {
  name: 'AppHeader',
  data() {
    return {
      drawer: null,
    }
  },
  created() {
    // Initialize the drawer state based on screen size
    console.log('user', localStorage.getItem('user'))
  },
  methods: {
    isActiveRoute(routePath) {
      return this.$route.path === routePath
    },
    goToPatients() {
      if (this.$route.path !== '/') {
        this.$router.push('/')
      }
    },
    goToDashboard() {
      if (this.$route.path !== '/dashboard-page') {
        this.$router.push('/dashboard-page')
      }
    },
    logout() {
      localStorage.removeItem('token')
      localStorage.removeItem('user')
      this.$router.push('/login')
    },
  },
}
</script>

<style scoped>
.sidebar-drawer {
  background: rgba(15, 14, 28, 0.95) !important;
  backdrop-filter: blur(24px);
  -webkit-backdrop-filter: blur(24px);
  border-right: 1px solid rgba(255, 255, 255, 0.08) !important;
}

.brand-container {
  border-bottom: 0px;
}

.logo-icon {
  filter: drop-shadow(0 0 8px rgba(6, 182, 212, 0.4));
}

.brand-text {
  line-height: 1.1;
}

.nav-item {
  border-radius: 8px !important;
  transition: all 0.2s ease !important;
  color: var(--text-secondary);
}

.nav-item:hover {
  background: rgba(255, 255, 255, 0.03) !important;
  color: var(--text-primary);
}

.border-cyan {
  border: 1.5px solid rgba(6, 182, 212, 0.3);
}

.user-details {
  line-height: 1.2;
}

.logout-btn {
  border: 1px solid rgba(244, 63, 94, 0.2);
}

/* Styled Top App Bar */
.app-top-bar {
  background: rgba(8, 7, 16, 0.8) !important;
  backdrop-filter: blur(16px);
  -webkit-backdrop-filter: blur(16px);
  border-bottom: 1px solid rgba(255, 255, 255, 0.08) !important;
}
</style>

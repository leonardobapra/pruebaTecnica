<template>
  <v-app>
    <!-- Top App Bar (Visible on ALL screens) -->
    <v-app-bar flat class="app-top-bar" height="64">
      <v-app-bar-nav-icon color="white" @click.stop="drawer = !drawer"></v-app-bar-nav-icon>
      
      <div class="d-flex align-center ml-2">
        <v-icon icon="mdi-heart-flash" color="cyan" size="28" class="logo-icon mr-2"></v-icon>
        <div class="brand-text d-flex flex-column">
          <span class="text-white font-weight-bold text-h6 leading-none">GoEcosystem</span>
          <span class="text-cyan font-weight-semibold text-caption mt-n1">DIGITAL HEALTH</span>
        </div>
      </div>

      <v-spacer></v-spacer>

      <!-- User Profile Dropdown in Top-Right -->
      <v-menu min-width="220px" rounded>
        <template v-slot:activator="{ props }">
          <v-btn icon v-bind="props" class="profile-btn mr-4">
            <v-avatar color="cyan-darken-3" size="38" class="elevation-1">
              <span class="text-white font-weight-bold">{{ userInitials }}</span>
            </v-avatar>
          </v-btn>
        </template>
        <v-card class="user-dropdown-card py-2" border>
          <v-card-text class="pa-4 text-center">
            <v-avatar color="cyan-darken-3" size="48" class="mb-2">
              <span class="text-white font-weight-bold text-h6">{{ userInitials }}</span>
            </v-avatar>
            <h3 class="text-subtitle-1 font-weight-bold text-white mb-0">
              {{ user ? user.name : 'Usuario Demo' }}
            </h3>
            <p class="text-caption text-grey mb-2">
              {{ user ? user.email : 'usuario@goecosystem.com' }}
            </p>
            <v-chip :color="userRoleColor" size="x-small" variant="flat" class="font-weight-bold mb-3">
              {{ userRoleName }}
            </v-chip>
            <v-divider class="my-2"></v-divider>
            <v-btn
              color="red-lighten-1"
              variant="text"
              block
              prepend-icon="mdi-logout"
              class="text-none font-weight-bold"
              @click="logout"
            >
              Cerrar Sesión
            </v-btn>
          </v-card-text>
        </v-card>
      </v-menu>
    </v-app-bar>

    <!-- Sidebar Navigation Drawer (Left Side, Toggled by App Bar icon) -->
    <v-navigation-drawer
      v-model="drawer"
      :temporary="!$vuetify.display.mdAndUp"
      class="sidebar-drawer"
      width="280"
      theme="dark"
      elevation="4"
    >
      <!-- Sidebar Brand Header -->
      <div class="brand-container pa-6 d-flex align-center">
        <v-icon icon="mdi-heart-flash" color="cyan" size="32" class="logo-icon mr-3"></v-icon>
        <div class="brand-text">
          <span class="text-white font-weight-bold text-subtitle-1">Módulos</span>
          <span class="text-cyan font-weight-semibold text-caption d-block mt-n1">SEGUIMIENTO</span>
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

      <!-- Footer Info / User display in Mobile Drawer -->
      <template v-slot:append>
        <div class="pa-4 user-profile-section border-top d-flex d-md-none align-center ga-3 mb-2">
          <v-avatar color="cyan-darken-3" size="36">
            <span class="text-white font-weight-bold">{{ userInitials }}</span>
          </v-avatar>
          <div class="overflow-hidden">
            <span class="text-caption font-weight-bold text-white d-block text-truncate leading-none mb-1">
              {{ user ? user.name : 'Usuario Demo' }}
            </span>
            <v-chip :color="userRoleColor" size="x-small" variant="flat" class="font-weight-bold">
              {{ userRoleName }}
            </v-chip>
          </div>
        </div>
        <div class="pa-4 text-center">
          <span class="text-caption text-grey-darken-1 font-weight-medium">
            Versión 1.0 (2026)
          </span>
        </div>
      </template>
    </v-navigation-drawer>

    <!-- Main Content with Defensive Reactive Styles -->
    <v-main class="main-content" :style="mainContentStyle">
      <router-view />
    </v-main>
  </v-app>
</template>

<script>
export default {
  name: 'MainLayout',
  data() {
    return {
      drawer: true,
      user: null
    }
  },
  computed: {
    mainContentStyle() {
      // Force correct layout padding-top and padding-left dynamically, bypass Vuetify rendering delays
      const isDesktop = this.$vuetify.display.mdAndUp
      const paddingLeft = (isDesktop && this.drawer) ? '280px' : '0px'
      return {
        paddingLeft: paddingLeft,
        paddingTop: '80px', // 64px height + 16px top buffer to ensure header is fully visible
        transition: 'padding-left 0.2s ease-in-out' // Smooth transition when toggling sidebar
      }
    },
    userInitials() {
      if (!this.user || !this.user.name) return 'U'
      const parts = this.user.name.split(' ')
      if (parts.length > 1) {
        return (parts[0][0] + parts[1][0]).toUpperCase()
      }
      return parts[0][0].toUpperCase()
    },
    userRoleName() {
      if (!this.user) return 'OPERADOR'
      return this.user.role === 'ADMIN' ? 'ADMINISTRADOR' : 'OPERADOR'
    },
    userRoleColor() {
      if (!this.user) return 'grey'
      return this.user.role === 'ADMIN' ? 'red' : 'cyan'
    }
  },
  mounted() {
    const userStr = localStorage.getItem('user')
    if (userStr) {
      this.user = JSON.parse(userStr)
    }
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
    }
  }
}
</script>

<style scoped>
.main-content {
  background-color: #080710;
  min-height: 100vh;
}

.app-top-bar {
  background: rgba(8, 7, 16, 0.8) !important;
  backdrop-filter: blur(16px);
  -webkit-backdrop-filter: blur(16px);
  border-bottom: 1px solid rgba(255, 255, 255, 0.08) !important;
}

.sidebar-drawer {
  background: rgba(15, 14, 28, 0.95) !important;
  backdrop-filter: blur(24px);
  -webkit-backdrop-filter: blur(24px);
  border-right: 1px solid rgba(255, 255, 255, 0.08) !important;
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

.profile-btn {
  border: 1px solid rgba(6, 182, 212, 0.2);
  transition: all 0.2s ease;
}

.profile-btn:hover {
  border-color: var(--accent-cyan);
  box-shadow: 0 0 10px rgba(6, 182, 212, 0.15);
}

.user-dropdown-card {
  background: rgba(15, 14, 28, 0.95) !important;
  backdrop-filter: blur(20px);
  border-color: rgba(255, 255, 255, 0.08) !important;
}

.leading-none {
  line-height: 1;
}

.border-top {
  border-top: 1px solid rgba(255, 255, 255, 0.08) !important;
}
</style>

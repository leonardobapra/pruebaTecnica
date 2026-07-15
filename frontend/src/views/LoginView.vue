<template>
  <v-container class="fill-height justify-center align-center px-4" fluid>
    <v-row justify="center" align="center" class="w-100 h-100">
      <v-col cols="12" sm="8" md="6" lg="4" class="d-flex justify-center">
        <v-card class="login-card glass-card pa-6 w-100" variant="outlined">
          <v-card-item class="text-center pb-4">
            <!-- Logo -->
            <v-icon icon="mdi-heart-flash" color="cyan" size="48" class="logo-icon mb-2"></v-icon>
            <v-card-title class="text-h5 font-weight-bold text-white mb-1">
              Iniciar Sesión
            </v-card-title>
            <v-card-subtitle class="text-caption text-grey-lighten-1">
              Accede al sistema de gestión de pacientes GoEcosystem
            </v-card-subtitle>
          </v-card-item>

          <v-card-text>
            <v-form @submit.prevent="handleLogin" ref="loginForm">
              <!-- Alert for error messages -->
              <v-alert
                v-if="error"
                type="error"
                variant="tonal"
                density="compact"
                class="mb-4 text-caption"
                closable
                @click:close="error = null"
              >
                {{ error }}
              </v-alert>

              <!-- Username Input -->
              <v-text-field
                v-model="username"
                label="Usuario"
                placeholder="admin.demo"
                prepend-inner-icon="mdi-account"
                variant="outlined"
                density="comfortable"
                class="mb-4"
                :rules="[(v) => !!v || 'El usuario es obligatorio']"
                required
                hide-details="auto"
              ></v-text-field>

              <!-- Password Input -->
              <v-text-field
                v-model="password"
                label="Contraseña"
                placeholder="••••••••"
                prepend-inner-icon="mdi-lock"
                :append-inner-icon="showPassword ? 'mdi-eye-off' : 'mdi-eye'"
                :type="showPassword ? 'text' : 'password'"
                variant="outlined"
                density="comfortable"
                class="mb-6"
                :rules="[(v) => !!v || 'La contraseña es obligatoria']"
                required
                hide-details="auto"
                @click:append-inner="showPassword = !showPassword"
              ></v-text-field>

              <!-- Submit Button -->
              <v-btn
                type="submit"
                color="cyan"
                size="large"
                block
                :loading="loading"
                class="text-none font-weight-bold text-white login-btn"
              >
                Ingresar
              </v-btn>
            </v-form>
          </v-card-text>

          <v-divider class="my-4"></v-divider>

          <!-- Demo Credentials Info -->
          <v-card-actions class="d-flex flex-column align-start pt-0">
            <span class="text-caption font-weight-bold text-grey-lighten-1 text-uppercase mb-2">
              Credenciales de Demostración:
            </span>
            <div class="demo-info-box w-100 pa-3 rounded-lg border">
              <div class="d-flex justify-space-between mb-1 text-caption">
                <span class="text-grey">Usuario Admin:</span>
                <code
                  class="text-cyan cursor-pointer font-weight-bold"
                  @click="fillCredentials('admin.demo', 'Demo2026*')"
                >
                  admin.demo
                </code>
              </div>
              <div class="d-flex justify-space-between text-caption">
                <span class="text-grey">Contraseña:</span>
                <code
                  class="text-cyan cursor-pointer font-weight-bold"
                  @click="fillCredentials('admin.demo', 'Demo2026*')"
                >
                  Demo2026*
                </code>
              </div>
            </div>
          </v-card-actions>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script>
import authApi from '@/api/auth'

export default {
  name: 'LoginView',
  data() {
    return {
      username: '',
      password: '',
      showPassword: false,
      loading: false,
      error: null,
    }
  },
  methods: {
    async handleLogin() {
      if (!this.username || !this.password) {
        this.error = 'Por favor complete todos los campos.'
        return
      }

      this.loading = true
      this.error = null

      try {
        const response = await authApi.login(this.username, this.password)
        localStorage.setItem('token', response.data.token)
        localStorage.setItem('user', JSON.stringify(response.data.user))
        console.log('Login successful:', response.data)
        this.$router.push('/')
      } catch (err) {
        console.error(err)
        if (err.response && err.response.status === 401) {
          this.error = 'Usuario o contraseña incorrectos.'
        } else {
          this.error =
            'No se pudo conectar con el servidor backend. Por favor, verifica que esté corriendo.'
        }
      } finally {
        this.loading = false
      }
    },
    fillCredentials(user, pass) {
      this.username = user
      this.password = pass
    },
  },
}
</script>

<style scoped>
.fill-height {
  min-height: 100vh;
  background-color: #080710;
}

.login-card {
  background: rgba(15, 14, 28, 0.7) !important;
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.08) !important;
  border-radius: 20px !important;
  box-shadow: 0 15px 35px rgba(0, 0, 0, 0.4) !important;
}

.logo-icon {
  filter: drop-shadow(0 0 10px rgba(6, 182, 212, 0.5));
  animation: float 4s ease-in-out infinite;
}

@keyframes float {
  0%,
  100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-6px);
  }
}

.login-btn {
  background: linear-gradient(135deg, #06b6d4, #3b82f6) !important;
  box-shadow: 0 4px 15px rgba(6, 182, 212, 0.25) !important;
}

.demo-info-box {
  background: rgba(255, 255, 255, 0.02);
  border-color: rgba(255, 255, 255, 0.05) !important;
}

code {
  background: rgba(255, 255, 255, 0.05);
  padding: 2px 6px;
  border-radius: 4px;
  font-family: monospace;
}
</style>

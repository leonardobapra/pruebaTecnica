<template>
  <v-container class="py-8" max-width="1200px">
    <!-- Header Section -->
    <v-card class="mb-6 glass-card header-border" variant="outlined">
      <v-card-text class="d-flex justify-space-between align-center flex-wrap ga-4">
        <div>
          <h1 class="text-h4 font-weight-bold text-white mb-1">
            <v-icon icon="mdi-account-multiple" class="mr-2 text-cyan" size="36"></v-icon>
            Gestión de Pacientes
          </h1>
          <p class="text-subtitle-2 text-grey-lighten-1">
            Seguimiento y control de la lista de espera médica
          </p>
        </div>
        <div class="d-flex align-center ga-3">
          <v-sheet class="stat-badge d-flex align-center px-4 py-2 rounded-lg" border>
            <span class="text-caption text-grey font-weight-bold text-uppercase mr-2">
              Total:
            </span>
            <span class="text-h6 text-cyan font-weight-bold">{{ totalElements }}</span>
          </v-sheet>
          <v-btn
            color="cyan"
            prepend-icon="mdi-plus"
            class="text-none font-weight-bold text-white create-btn"
            height="44"
            @click="openCreateModal"
          >
            Nuevo Paciente
          </v-btn>
        </div>
      </v-card-text>
    </v-card>

    <!-- Filters Section -->
    <v-card class="mb-6 glass-card" variant="outlined">
      <v-card-text>
        <v-row align="end">
          <v-col cols="12" md="5" class="py-2">
            <div class="text-caption font-weight-bold text-uppercase text-grey-lighten-1 mb-2">
              Buscar Paciente
            </div>
            <v-text-field
              v-model="search"
              placeholder="Buscar por nombre o documento..."
              prepend-inner-icon="mdi-magnify"
              variant="outlined"
              density="comfortable"
              hide-details
              @input="onSearchInput"
            ></v-text-field>
          </v-col>

          <v-col cols="12" md="3" class="py-2">
            <div class="text-caption font-weight-bold text-uppercase text-grey-lighten-1 mb-2">
              Estado
            </div>
            <v-select
              v-model="status"
              :items="statusOptions"
              item-title="title"
              item-value="value"
              placeholder="Todos los estados"
              variant="outlined"
              density="comfortable"
              hide-details
              @update:model-value="applyFilters"
            ></v-select>
          </v-col>

          <v-col cols="12" md="3" class="py-2">
            <div class="text-caption font-weight-bold text-uppercase text-grey-lighten-1 mb-2">
              Prioridad
            </div>
            <v-select
              v-model="priority"
              :items="priorityOptions"
              item-title="title"
              item-value="value"
              placeholder="Todas las prioridades"
              variant="outlined"
              density="comfortable"
              hide-details
              @update:model-value="applyFilters"
            ></v-select>
          </v-col>

          <v-col cols="12" md="1" class="py-2 text-right">
            <v-btn
              variant="text"
              color="grey-lighten-1"
              block
              @click="clearFilters"
              class="text-none font-weight-bold"
              style="height: 48px"
            >
              Limpiar
            </v-btn>
          </v-col>
        </v-row>
      </v-card-text>
    </v-card>

    <!-- Main List/Table Section -->
    <v-card class="glass-card" variant="outlined">
      <v-card-text class="pa-0">
        <!-- Loading State -->
        <div v-if="loading" class="d-flex flex-column align-center justify-center py-12 text-center">
          <v-progress-circular indeterminate color="cyan" size="50" class="mb-4"></v-progress-circular>
          <span class="text-body-1 text-grey-lighten-1">Cargando lista de pacientes...</span>
        </div>

        <!-- Error State -->
        <div v-else-if="error" class="d-flex flex-column align-center justify-center py-12 text-center px-4">
          <v-icon icon="mdi-alert-circle" color="red-accent-2" size="48" class="mb-3"></v-icon>
          <p class="text-body-1 text-red-accent-1 font-weight-medium max-width-error mb-4">
            {{ error }}
          </p>
          <v-btn color="cyan" class="text-none font-weight-bold text-white" @click="fetchPatients">
            Reintentar
          </v-btn>
        </div>

        <!-- Content -->
        <div v-else class="pa-4">
          <!-- Patient Table Component -->
          <PatientTable
            :patients="patients"
            @edit="openEditModal"
            @delete="openDeleteModal"
          />

          <!-- Pagination Controls -->
          <div v-if="totalPages > 1" class="d-flex justify-space-between align-center flex-wrap mt-6 pt-4 border-top">
            <span class="text-body-2 text-grey-lighten-1 mb-2">
              Mostrando página <strong>{{ page + 1 }}</strong> de <strong>{{ totalPages }}</strong>
            </span>
            <v-pagination
              v-model="displayPage"
              :length="totalPages"
              :total-visible="5"
              active-color="cyan"
              density="comfortable"
              class="mb-2"
            ></v-pagination>
          </div>
        </div>
      </v-card-text>
    </v-card>

    <!-- CREATE & EDIT PATIENT DIALOG MODAL -->
    <v-dialog v-model="dialog" max-width="650px" persistent>
      <v-card class="modal-card border" border theme="dark">
        <v-card-title class="d-flex justify-space-between align-center pa-6 pb-2">
          <span class="text-h5 font-weight-bold text-white">
            <v-icon :icon="isEdit ? 'mdi-pencil' : 'mdi-account-plus'" color="cyan" class="mr-2"></v-icon>
            {{ isEdit ? 'Editar Paciente' : 'Registrar Paciente' }}
          </span>
          <v-btn icon="mdi-close" variant="text" color="grey" @click="dialog = false"></v-btn>
        </v-card-title>

        <v-card-text class="px-6 py-4">
          <v-form ref="dialogForm" v-model="formValid">
            <!-- Form Validation Error -->
            <v-alert
              v-if="formError"
              type="error"
              variant="tonal"
              density="compact"
              class="mb-4 text-caption"
            >
              {{ formError }}
            </v-alert>

            <v-row>
              <!-- Full Name -->
              <v-col cols="12" sm="7" class="py-1">
                <v-text-field
                  v-model="form.fullName"
                  label="Nombre Completo"
                  variant="outlined"
                  density="comfortable"
                  :rules="[v => !!v || 'El nombre completo es obligatorio']"
                  required
                ></v-text-field>
              </v-col>

              <!-- Email -->
              <v-col cols="12" sm="5" class="py-1">
                <v-text-field
                  v-model="form.email"
                  label="Correo Electrónico (Opcional)"
                  variant="outlined"
                  density="comfortable"
                  type="email"
                  :rules="[v => !v || /.+@.+\..+/.test(v) || 'Formato de correo no válido']"
                ></v-text-field>
              </v-col>

              <!-- Document Type -->
              <v-col cols="12" sm="4" class="py-1">
                <v-select
                  v-model="form.documentType"
                  :items="documentTypeOptions"
                  label="Tipo de Documento"
                  variant="outlined"
                  density="comfortable"
                  :rules="[v => !!v || 'Requerido']"
                  required
                ></v-select>
              </v-col>

              <!-- Document Number -->
              <v-col cols="12" sm="8" class="py-1">
                <v-text-field
                  v-model="form.documentNumber"
                  label="Número de Documento"
                  variant="outlined"
                  density="comfortable"
                  :rules="[v => !!v || 'El documento es obligatorio']"
                  required
                ></v-text-field>
              </v-col>

              <!-- Birth Date -->
              <v-col cols="12" sm="6" class="py-1">
                <v-text-field
                  v-model="form.birthDate"
                  label="Fecha de Nacimiento"
                  type="date"
                  variant="outlined"
                  density="comfortable"
                  :rules="[
                    v => !!v || 'La fecha de nacimiento es obligatoria',
                    v => new Date(v) <= new Date() || 'La fecha no puede ser futura'
                  ]"
                  required
                ></v-text-field>
              </v-col>

              <!-- Gender -->
              <v-col cols="12" sm="6" class="py-1">
                <v-select
                  v-model="form.gender"
                  :items="genderOptions"
                  label="Género"
                  variant="outlined"
                  density="comfortable"
                  :rules="[v => !!v || 'El género es obligatorio']"
                  required
                ></v-select>
              </v-col>

              <!-- Phone -->
              <v-col cols="12" sm="6" class="py-1">
                <v-text-field
                  v-model="form.phone"
                  label="Teléfono"
                  variant="outlined"
                  density="comfortable"
                  :rules="[v => !!v || 'El teléfono es obligatorio']"
                  required
                ></v-text-field>
              </v-col>

              <!-- City -->
              <v-col cols="12" sm="6" class="py-1">
                <v-text-field
                  v-model="form.city"
                  label="Ciudad (Opcional)"
                  variant="outlined"
                  density="comfortable"
                ></v-text-field>
              </v-col>

              <!-- EPS Catalog -->
              <v-col cols="12" sm="6" class="py-1">
                <v-select
                  v-model="form.epsCode"
                  :items="epsList"
                  item-title="name"
                  item-value="code"
                  label="EPS"
                  placeholder="Seleccione la EPS"
                  variant="outlined"
                  density="comfortable"
                  :rules="[v => !!v || 'La EPS es obligatoria']"
                  required
                ></v-select>
              </v-col>

              <!-- Priority -->
              <v-col cols="12" sm="3" class="py-1">
                <v-select
                  v-model="form.priority"
                  :items="['Alta', 'Media', 'Baja']"
                  label="Prioridad"
                  variant="outlined"
                  density="comfortable"
                  :rules="[v => !!v || 'Requerido']"
                  required
                ></v-select>
              </v-col>

              <!-- Status -->
              <v-col cols="12" sm="3" class="py-1">
                <v-select
                  v-model="form.status"
                  :items="['Pendiente', 'En atención', 'Atendido']"
                  label="Estado"
                  variant="outlined"
                  density="comfortable"
                  :rules="[v => !!v || 'Requerido']"
                  required
                ></v-select>
              </v-col>
            </v-row>
          </v-form>
        </v-card-text>

        <v-card-actions class="px-6 py-4 justify-end ga-2">
          <v-btn
            variant="text"
            color="grey-lighten-1"
            class="text-none font-weight-bold"
            @click="dialog = false"
          >
            Cancelar
          </v-btn>
          <v-btn
            color="cyan"
            class="text-none font-weight-bold text-white px-6 save-btn"
            :loading="formLoading"
            @click="savePatient"
          >
            Guardar
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <!-- DELETE CONFIRMATION DIALOG -->
    <v-dialog v-model="deleteDialog" max-width="450px">
      <v-card theme="dark" class="modal-card border pa-4">
        <v-card-title class="d-flex align-center pb-2">
          <v-icon icon="mdi-alert" color="red-lighten-1" class="mr-2" size="28"></v-icon>
          Confirmar Eliminación
        </v-card-title>
        <v-card-text class="py-3 text-body-1 text-grey-lighten-2">
          ¿Está seguro de que desea eliminar este paciente de la lista de espera? Esta acción no se puede deshacer.
        </v-card-text>
        <v-card-actions class="justify-end ga-2">
          <v-btn
            variant="text"
            color="grey-lighten-1"
            class="text-none font-weight-bold"
            @click="deleteDialog = false"
          >
            Cancelar
          </v-btn>
          <v-btn
            color="red-lighten-1"
            class="text-none font-weight-bold text-white px-4"
            :loading="deleteLoading"
            @click="confirmDelete"
          >
            Eliminar Paciente
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-container>
</template>

<script>
import patientsApi from '@/api/patients'
import PatientTable from '@/components/PatientTable.vue'

export default {
  name: 'PatientsView',
  components: {
    PatientTable
  },
  data() {
    return {
      patients: [],
      epsList: [],
      search: '',
      status: '',
      priority: '',
      page: 0,
      size: 10,
      totalPages: 0,
      totalElements: 0,
      loading: false,
      error: null,
      searchTimeout: null,

      // Dialog Modal Controls
      dialog: false,
      isEdit: false,
      selectedId: null,
      formValid: false,
      formLoading: false,
      formError: null,

      // Delete Modal Controls
      deleteDialog: false,
      deleteId: null,
      deleteLoading: false,

      // Form Object
      form: {
        documentType: 'CC',
        documentNumber: '',
        fullName: '',
        birthDate: '',
        gender: 'Femenino',
        phone: '',
        email: '',
        epsCode: '',
        city: '',
        priority: 'Alta',
        status: 'Pendiente'
      },

      // Catalog Options
      statusOptions: [
        { title: 'Todos los estados', value: '' },
        { title: 'Pendiente', value: 'Pendiente' },
        { title: 'En atención', value: 'En atención' },
        { title: 'Atendido', value: 'Atendido' }
      ],
      priorityOptions: [
        { title: 'Todas las prioridades', value: '' },
        { title: 'Alta', value: 'Alta' },
        { title: 'Media', value: 'Media' },
        { title: 'Baja', value: 'Baja' }
      ],
      documentTypeOptions: ['CC', 'TI', 'CE', 'PA'],
      genderOptions: ['Femenino', 'Masculino', 'Otro', 'Prefiere no informar']
    }
  },
  computed: {
    displayPage: {
      get() {
        return this.page + 1
      },
      set(val) {
        this.page = val - 1
        this.fetchPatients()
      }
    }
  },
  mounted() {
    this.fetchPatients()
    this.fetchEps()
  },
  methods: {
    async fetchPatients() {
      this.loading = true
      this.error = null
      try {
        const params = {
          search: this.search,
          status: this.status,
          priority: this.priority,
          page: this.page,
          size: this.size,
          sort: 'id,desc'
        }
        const response = await patientsApi.getPatients(params)
        this.patients = response.data.content || []
        this.totalPages = response.data.totalPages || 0
        this.totalElements = response.data.totalElements || 0
      } catch (err) {
        console.error(err)
        this.error = 'Ocurrió un error al conectar con el servidor. Por favor, asegúrate de que el backend esté ejecutándose.'
      } finally {
        this.loading = false
      }
    },
    async fetchEps() {
      try {
        const response = await patientsApi.getEpsList()
        this.epsList = response.data || []
      } catch (err) {
        console.error('Error al cargar catálogo de EPS:', err)
      }
    },
    applyFilters() {
      this.page = 0
      this.fetchPatients()
    },
    onSearchInput() {
      clearTimeout(this.searchTimeout)
      this.searchTimeout = setTimeout(() => {
        this.applyFilters()
      }, 350)
    },
    clearFilters() {
      this.search = ''
      this.status = ''
      this.priority = ''
      this.applyFilters()
    },

    // Modal Operations
    openCreateModal() {
      this.isEdit = false
      this.selectedId = null
      this.form = {
        documentType: 'CC',
        documentNumber: '',
        fullName: '',
        birthDate: '',
        gender: 'Femenino',
        phone: '',
        email: '',
        epsCode: '',
        city: '',
        priority: 'Alta',
        status: 'Pendiente'
      }
      this.formError = null
      this.dialog = true
      this.$nextTick(() => {
        if (this.$refs.dialogForm) {
          this.$refs.dialogForm.resetValidation()
        }
      })
    },
    openEditModal(patient) {
      this.isEdit = true
      this.selectedId = patient.id
      this.form = {
        documentType: patient.documentType,
        documentNumber: patient.documentNumber,
        fullName: patient.fullName,
        birthDate: patient.birthDate,
        gender: patient.gender,
        phone: patient.phone,
        email: patient.email || '',
        epsCode: patient.epsCode,
        city: patient.city || '',
        priority: patient.priority,
        status: patient.status
      }
      this.formError = null
      this.dialog = true
      this.$nextTick(() => {
        if (this.$refs.dialogForm) {
          this.$refs.dialogForm.resetValidation()
        }
      })
    },
    async savePatient() {
      // Validate form triggers
      const { valid } = await this.$refs.dialogForm.validate()
      if (!valid) return

      this.formLoading = true
      this.formError = null

      try {
        if (this.isEdit) {
          await patientsApi.updatePatient(this.selectedId, this.form)
        } else {
          await patientsApi.createPatient(this.form)
        }
        this.fetchPatients()
        this.dialog = false
      } catch (err) {
        console.error(err)
        if (err.response && err.response.data && err.response.data.message) {
          this.formError = err.response.data.message
        } else {
          this.formError = 'Ocurrió un error al guardar los datos del paciente.'
        }
      } finally {
        this.formLoading = false
      }
    },

    // Delete Operations
    openDeleteModal(id) {
      this.deleteId = id
      this.deleteDialog = true
    },
    async confirmDelete() {
      this.deleteLoading = true
      try {
        await patientsApi.deletePatient(this.deleteId)
        this.fetchPatients()
        this.deleteDialog = false
      } catch (err) {
        console.error(err)
        alert('Ocurrió un error al intentar eliminar el paciente.')
      } finally {
        this.deleteLoading = false
      }
    }
  }
}
</script>

<style scoped>
.glass-card {
  background: rgba(255, 255, 255, 0.02) !important;
  backdrop-filter: blur(16px);
  border: 1px solid rgba(255, 255, 255, 0.08) !important;
  border-radius: 16px !important;
  box-shadow: 0 8px 32px 0 rgba(0, 0, 0, 0.3) !important;
}

.header-border {
  border-left: 4px solid var(--accent-cyan) !important;
}

.stat-badge {
  background: rgba(255, 255, 255, 0.04) !important;
  border: 1px solid rgba(255, 255, 255, 0.08) !important;
  box-shadow: 0 0 10px rgba(6, 182, 212, 0.05);
}

.border-top {
  border-top: 1px solid rgba(255, 255, 255, 0.08) !important;
}

.max-width-error {
  max-width: 450px;
}

.create-btn {
  background: linear-gradient(135deg, #06b6d4, #3b82f6) !important;
  box-shadow: 0 4px 15px rgba(6, 182, 212, 0.25) !important;
}

/* Modal specific card customization */
.modal-card {
  background: rgba(15, 14, 28, 0.98) !important;
  backdrop-filter: blur(20px);
  border-color: rgba(255, 255, 255, 0.08) !important;
  border-radius: 16px !important;
}

.save-btn {
  background: linear-gradient(135deg, #06b6d4, #3b82f6) !important;
}
</style>

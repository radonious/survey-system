<template>
  <div class="container mt-5">
    <h1 class="text-center mb-4">Список опросов</h1>
    <div v-if="forms.length === 0" class="text-center">
      <p>Нет доступных опросов</p>
    </div>
    <div v-else>
      <div v-for="form in forms" :key="form.id" class="mb-3">
        <div class="card" :class="{ 'bg-light': form.completed }">
          <div class="card-body">
            <h5 class="card-title">{{ form.title }}</h5>
            <p class="card-text">{{ form.description }}</p>
            <div v-if="userRole === 'USER'">
              <router-link v-if="!form.completed" :to="`/complete-form/${form.id}`" class="btn btn-primary">
                Заполнить опрос
              </router-link>
              <router-link v-else :to="`/my-answers/${form.completionId}`" class="btn btn-secondary">
                Посмотреть мои ответы
              </router-link>
            </div>
            <div v-else-if="userRole === 'CREATOR'">
              <router-link :to="`/statistics/${form.id}`" class="btn btn-primary">
                Посмотреть статистику
              </router-link>
            </div>
          </div>
        </div>
      </div>
    </div>
    <div v-if="userRole === 'CREATOR'" class="text-center mb-4">
      <router-link to="/create-form" class="btn btn-success">Создать опрос</router-link>
    </div>
  </div>
</template>

<script>
import { API_BASE_URL } from '../../config.js';
import { authService } from '../service/authService.js';

export default {
  data() {
    return {
      forms: [],
      userRole: null,
    };
  },
  async created() {
    this.userRole = authService.getUserRole();
    await this.fetchForms();
  },
  methods: {
    // Метод сбора форм
    async fetchForms() {
      try {
        const response = await authService.fetchWithToken(`${API_BASE_URL}/forms`);
        if (!response.ok) {
          const errorData = await response.json();
          throw new Error(errorData);
        }
        const data = await response.json();
        if (authService.getUserRole() === 'USER') {
          for (const form of data) {
            await this.checkFormCompletion(form);
          }
        }
        this.forms = data;
      } catch (error) {
        console.error('Ошибка при загрузке списка опросов:', error);
      }
    },

    async checkFormCompletion(form) {
      try {
        const userId = authService.getUserId();
        const response = await authService.fetchWithToken(
          `${API_BASE_URL}/completions/user-form-completion?formId=${form.id}&userId=${userId}`
        );
        if (response.ok) {
          const completionId = await response.json();
          form.completed = true;
          form.completionId = completionId;
        } else {
          if (response.status !== 404) {
            const errorData = await response.json();
            throw new Error(errorData);
          }
          form.completed = false;
        }
      } catch (error) {
        console.error('Ошибка при проверке прохождения опроса:', error);
      }
    },
  },
};
</script>

<style scoped>
.bg-light {
  opacity: 0.6;
}
</style>
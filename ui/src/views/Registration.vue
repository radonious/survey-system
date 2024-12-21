<template>
  <div class="container mt-5">
    <div class="row justify-content-center">
      <div class="col-md-6">
        <div class="card">
          <div class="card-header">
            <h3 class="text-center">Registration</h3>
          </div>
          <div class="card-body">
            <form @submit.prevent="handleRegistration">
              <div class="mb-3">
                <label for="username" class="form-label">Username</label>
                <input type="text" class="form-control" id="username" v-model="username" placeholder="ivanov123"
                  required>
              </div>
              <div class="mb-3">
                <label for="password" class="form-label">Password</label>
                <input type="password" class="form-control" id="password" v-model="password" required>
              </div>
              <div class="mb-3">
                <label for="confirmPassword" class="form-label">Confirm Password</label>
                <input type="password" class="form-control" id="confirmPassword" v-model="confirmPassword" required>
              </div>
              <button type="submit" class="btn btn-primary">Register</button>
              <div v-if="errorMessage" class="text-danger mt-2">{{ errorMessage }}</div>
            </form>
          </div>
        </div>
      </div>
    </div>
    <br>
  </div>
</template>

<script>
import { API_BASE_URL } from '../../config.js';

export default {
  name: 'Registration',
  data() {
    return {
      username: '', // Имя пользователя
      password: '', // Пароль
      confirmPassword: '', // Подтверждение пароля
      errorMessage: '' // Сообщение об ошибке
    };
  },

  methods: {
    // Метод обработки регистрации
    async handleRegistration() {
      this.errorMessage = ''; // Очистка сообщения об ошибке

      // Проверка совпадения паролей
      if (this.password !== this.confirmPassword) {
        this.errorMessage = 'Passwords do not match';
        return;
      }

      try {
        // Логирование запроса
        console.log('Sending registration request:', {
          username: this.username,
          password: this.password
        });

        // Отправка запроса на регистрацию
        const response = await fetch(`${API_BASE_URL}/auth/register`, {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json'
          },
          body: JSON.stringify({ username: this.username, password: this.password })
        });

        // Проверка статуса ответа
        if (!response.ok) {
          const errorData = await response.text();
          this.errorMessage = errorData;
          return;
        }

        // Перенаправление на страницу входа после успешной регистрации
        this.$router.push('/login');

      } catch (error) {
        // Обработка ошибок
        this.errorMessage = error.message;
      }
    }
  }
}
</script>
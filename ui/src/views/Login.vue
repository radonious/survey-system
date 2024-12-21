<template>
  <div class="container mt-5">
    <div class="row justify-content-center">
      <div class="col-md-6">
        <div class="card">
          <div class="card-header">
            <h3 class="text-center">Login</h3>
          </div>
          <div class="card-body">
            <form @submit.prevent="handleLogin">
              <div class="mb-3">
                <label for="username" class="form-label">Username</label>
                <input type="text" class="form-control" id="username" v-model="username" placeholder="ivanov123"
                  required>
              </div>
              <div class="mb-3">
                <label for="password" class="form-label">Password</label>
                <input type="password" class="form-control" id="password" v-model="password" required>
              </div>
              <button type="submit" class="btn btn-primary">Login</button>
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
import { authService } from '@/service/authService.js';
import { API_BASE_URL } from '../../config.js';

export default {
  name: 'Login',
  data() {
    return {
      username: '',
      password: '',
      errorMessage: ''
    };
  },
  methods: {
    async handleLogin() {
      this.errorMessage = '';
      try {
        const response = await fetch(`${API_BASE_URL}/auth/login`, {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json'
          },
          body: JSON.stringify({ username: this.username, password: this.password })
        });
        if (!response.ok) {
          const errorData = await response.text();
          this.errorMessage = errorData;
          return;
        }
        const data = await response.json();
        authService.setTokens(data.accessToken, data.refreshToken, data.role, data.userId, data.username);
        console.log(authService.getTokens())
        this.$router.push('/form-list');
      } catch (error) {
        this.errorMessage = error.message;
      }
    }
  }
}
</script>
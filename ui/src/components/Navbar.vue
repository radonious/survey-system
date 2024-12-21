<template>
  <nav class="navbar navbar-expand-lg navbar-light bg-light">
    <div class="container-fluid">
      <a class="navbar-brand" href="#">Eltex-forms</a>
      <button class="navbar-toggler" type="button" data-bs-toggle="collapse"
        data-bs-target="#navbarSupportedContent" aria-controls="navbarSupportedContent" aria-expanded="false"
        aria-label="Toggle navigation">
        <span class="navbar-toggler-icon"></span>
      </button>
      <div class="collapse navbar-collapse" id="navbarSupportedContent">
        <ul class="navbar-nav me-auto mb-2 mb-lg-0">
          <li class="nav-item" v-if="isAuthenticated">
            <router-link to="/form-list" class="nav-link active" aria-current="page">Список опросов</router-link>
          </li>
          <li class="nav-item" v-if="isAuthenticated">
            <router-link to="/profile" class="nav-link">Моя страница</router-link>
          </li>
        </ul>
      </div>
    </div>
  </nav>
</template>

<script>
import { authService } from '@/service/authService';

export default {
  name: 'Navbar',
  data() {
    return {
      isAuthenticated: authService.hasRefreshToken()
    };
  },
  watch: {
    '$route': function() {
      this.updateAuthenticationStatus();
    }
  },
  methods: {
    updateAuthenticationStatus() {
      this.isAuthenticated = authService.hasRefreshToken();
    }
  },
  created() {
    this.updateAuthenticationStatus();
  }
}
</script>
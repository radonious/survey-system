<template>
    <div class="container mt-5">
      <div class="row justify-content-center">
        <div class="col-md-6">
          <div class="card">
            <div class="card-header">
              <h3 class="text-center">Добро пожаловать на Eltex-forms</h3>
            </div>
            <div class="card-body">
              <p class="text-center">Пожалуйста, войдите или зарегистрируйтесь, чтобы продолжить.</p>
              <div class="d-grid gap-2">
                <router-link to="/login" class="btn btn-primary">Войти</router-link>
                <router-link to="/registration" class="btn btn-secondary">Зарегистрироваться</router-link>
              </div>
            </div>
          </div>
        </div>
      </div>
      <br>
    </div>
  </template>
  
  <script>
  export default {
    name: 'WelcomePage'
  }
  </script>
  
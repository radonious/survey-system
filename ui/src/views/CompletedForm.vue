<template>
    <div class="container mt-5">
      <h1 v-if="form.title" class="text-center mb-4">{{ form.title }}</h1>
      <p v-if="form.description" class="text-center mb-4">{{ form.description }}</p>
      <div v-for="(question) in form.questions" :key="question.id" class="mb-3">
        <div class="card">
          <div class="card-body">
            <h5 class="card-title">{{ question.text }}</h5>
            <div v-if="question.type === 'TEXT' || question.type === 'NUMERIC'">
              <p class="card-text">{{ getAnswerText(question.id) }}</p>
            </div>
            <div v-else-if="question.type === 'SINGLE_CHOICE'">
              <p class="card-text">
                Выбранный вариант: {{ getSelectedOptionText(question.id) }}
              </p>
            </div>
            <div v-else-if="question.type === 'MULTIPLE_CHOICE'">
              <p class="card-text">
                Выбранные варианты:
                <ul>
                  <li v-for="option in getSelectedOptions(question.id)" :key="option.id">
                    {{ option.text }}
                  </li>
                </ul>
              </p>
            </div>
            <div v-else-if="question.type === 'RATING'">
              <p class="card-text">
                Составленный рейтинг:
                <ul>
                  <li v-for="option in getSelectedOptions(question.id)" :key="option.id">
                    {{ option.text }}
                  </li>
                </ul>
              </p>
            </div>
          </div>
        </div>
      </div>
      <div class="d-flex justify-content-center mt-4">
        <button @click="goBack" class="btn btn-secondary">Назад</button>
      </div>
      <br>
    </div>
  </template>
  
  <script>
  import { API_BASE_URL } from '../../config.js';
  import { authService } from '@/service/authService';
  
  export default {
    data() {
      return {
        form: {
          id: null,
          title: '',
          description: '',
          questions: [],
        },
        completion: {
          id: null,
          userId: null,
          formId: null,
          answers: [],
        },
      };
    },
    async created() {
      const completionId = this.$route.params.completionId;
      await this.fetchCompletion(completionId);
      await this.fetchForm(this.completion.formId);
    },
    methods: {
      // Метод получения данных о пройденном опросе
      async fetchCompletion(completionId) {
        try {
          const response = await authService.fetchWithToken(`${API_BASE_URL}/completions/${completionId}`);
          if (!response.ok) {
            const errorData = await response.json();
            throw new Error(errorData.message);
          }
          const data = await response.json();
          this.completion = data;
        } catch (error) {
          console.error('Ошибка при загрузке пройденного опроса:', error);
        }
      },
      // Метод получения данных о форме
      async fetchForm(formId) {
        try {
          const response = await authService.fetchWithToken(`${API_BASE_URL}/forms/${formId}`);
          if (!response.ok) {
            const errorData = await response.json();
            throw new Error(errorData.message);
          }
          const data = await response.json();
          this.form = data;
        } catch (error) {
          console.error('Ошибка при загрузке формы:', error);
        }
      },
      // Метод получения текстового ответа
      getAnswerText(questionId) {
        const answer = this.completion.answers.find(a => a.questionId === questionId);
        return answer ? answer.answerText : 'Нет ответа';
      },
      // Метод получения текста выбранного варианта (для SINGLE_CHOICE)
      getSelectedOptionText(questionId) {
        const answer = this.completion.answers.find(a => a.questionId === questionId);
        return answer && answer.selectedOptions.length > 0
          ? answer.selectedOptions[0].text
          : 'Нет ответа';
      },
      // Метод получения текста выбранных вариантов (для MULTIPLE_CHOICE)
      getSelectedOptions(questionId) {
        const answer = this.completion.answers.find(a => a.questionId === questionId);
        return answer ? answer.selectedOptions : [];
      },
      // Метод для перехода назад
      goBack() {
        this.$router.push('/form-list');
      },
    },
  };
  </script>
  
 
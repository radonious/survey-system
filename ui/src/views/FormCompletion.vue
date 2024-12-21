<template>
    <div class="container mt-5">
        <h1 v-if="form.title" class="text-center mb-4">{{ form.title }}</h1>
        <p v-if="form.description" class="text-center mb-4">{{ form.description }}</p>
        <div v-for="(question, index) in form.questions" :key="question.id" class="mb-3">
            <QuestionComponent :question="question" :index="index" ref="questions"
                @update:answer="updateAnswer(index, $event)" />
        </div>
        <div class="d-flex justify-content-center">
            <button @click="submitForm" class="btn btn-success">Завершить опрос</button>
        </div>
        <br>
    </div>
</template>

<script>
import QuestionComponent from '../components/QuestionCompletion.vue';
import { API_BASE_URL } from '../../config.js';
import { authService } from '@/service/authService';

export default {
    components: {
        QuestionComponent,
    },
    data() {
        return {
            form: {
                id: null,
                creatorId: null,
                creatorName: '',
                title: '',
                description: '',
                questions: [],
            },
            answers: [],
        };
    },
    async created() {
        const formId = this.$route.params.formId;
        await this.fetchForm(formId);
    },
    methods: {
        //метод получения формы
        async fetchForm(formId) {
            try {
                const response = await authService.fetchWithToken(`${API_BASE_URL}/forms/${formId}`);
                if (!response.ok) {
                    const errorData = await response.json();
                    throw new Error(errorData.message)
                }
                const data = await response.json();
                this.form = data;
            } catch (error) {
                console.error('Ошибка при загрузке опроса:', error);
            }
        },
        updateAnswer(index, answer) {
          this.answers[index] = answer;
        },
        submitForm() {
            const unansweredQuestions = this.$refs.questions.filter((questionComponent, index) => {
             const question = questionComponent.$props.question;
          const answer = questionComponent.answer;

          if (question.type === 'NUMERIC' || question.type === 'TEXT') {
            return !answer;
          } else if (question.type === 'SINGLE_CHOICE') {
            return answer === null || answer === undefined;
          } else if (question.type === 'MULTIPLE_CHOICE') {
            return !Array.isArray(answer) || answer.length === 0;
          } else if (question.type === 'RATING') {
            return !Array.isArray(answer) || answer.length !== question.options.length;
          }

          return false;
        });

        if (unansweredQuestions.length > 0) {
          alert('Пожалуйста, ответьте на все вопросы перед завершением опроса.');
          return;
        }

        this.answers = this.$refs.questions.map((questionComponent) => {
          const question = questionComponent.$props.question;
          const answer = questionComponent.answer;

          if (question.type === 'NUMERIC' || question.type === 'TEXT') {
            return {
              questionId: question.id,
              answerText: answer,
              selectedOptions: null,
            };
          } else if (question.type === 'SINGLE_CHOICE') {
            const selectedOption = question.options.find(option => option.id === answer);
            return {
              questionId: question.id,
              answerText: null,
              selectedOptions: selectedOption ? [{text: selectedOption.text}] : [],
            };
          } else if (question.type === 'MULTIPLE_CHOICE') {
            const selectedOptions = answer.map(optionId => {
              const option = question.options.find(opt => opt.id === optionId);
              return {text: option.text};
            });
            return {
              questionId: question.id,
              answerText: null,
              selectedOptions: selectedOptions,
            };
          } else if (question.type === 'RATING') {
            const selectedOptions = answer.map(optionId => {
              const option = question.options.find(opt => opt.id === optionId);
              return {text: option.text};
            });
            return {
              questionId: question.id,
              answerText: null,
              selectedOptions: selectedOptions,
            };
          }
        });

        const completionRequest = {
          answers: this.answers,
          userId: authService.getUserId(),
          formId: this.form.id,
        };

        console.log('Завершение опроса с ответами:', completionRequest);
        this.sendCompletionData(completionRequest);
      },
      async sendCompletionData(completionRequest) {
        try {
          console.log(completionRequest)
          const response = await authService.fetchWithToken(`${API_BASE_URL}/completions`, {
            method: 'POST',
            headers: {
              'Content-Type': 'application/json',
            },
            body: JSON.stringify(completionRequest),
          });

          if (!response.ok) {
            const errorData = await response.json();
            throw new Error(errorData.message)
          }

          const data = await response.json();
          console.log('Ответы успешно отправлены:', data);

          this.$router.push('/form-list');
        } catch (error) {
          console.error('Ошибка при отправке ответов:', error);
        }
      },
    },
  };
  </script>
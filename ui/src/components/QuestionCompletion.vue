<template>
  <div class="card mb-3">
    <div class="card-body">
      <h5 class="card-title">{{ index + 1 }}. {{ question.text }}</h5>
      <div v-if="question.imageUrl" class="mb-2">
        <img :src="ROOT_URL() + question.imageUrl" alt="Question Image" style="max-width: 100%;" />
      </div>
      <!-- Числовой тип вопроса -->
      <div v-if="question.type === 'NUMERIC'">
        <input type="number" class="form-control" v-model.number="answer" placeholder="Введите число" />
      </div>
      <!-- Текстовый тип вопроса -->
      <div v-else-if="question.type === 'TEXT'">
        <textarea class="form-control" v-model="answer" placeholder="Введите текст"></textarea>
      </div>
      <!-- Вопрос с одним вариантом -->
      <div v-else-if="question.type === 'SINGLE_CHOICE'">
        <div v-for="option in question.options" :key="option.id" class="form-check">
          <input class="form-check-input" type="radio" :name="'question' + question.id"
                 :id="'option' + option.id" v-model="answer" :value="option.id" />
          <label class="form-check-label" :for="'option' + option.id">
            {{ option.text }}
          </label>
        </div>
      </div>
      <!-- Вопрос с несколькими вариантами ответов -->
      <div v-else-if="question.type === 'MULTIPLE_CHOICE'">
        <div v-for="option in question.options" :key="option.id" class="form-check">
          <input class="form-check-input" type="checkbox" :id="'option' + option.id" v-model="answer"
                 :value="option.id" />
          <label class="form-check-label" :for="'option' + option.id">
            {{ option.text }}
          </label>
        </div>
      </div>
      <!-- Вопрос с рейтингом -->
      <div v-else-if="question.type === 'RATING'">
        <div v-for="(option, index) in sortedOptions" :key="option.id" class="mb-2 d-flex align-items-center">
          <span class="me-2">{{ index + 1 }}.</span>
          <span>{{ option.text }}</span>
          <button @click="moveOptionUp(index)" class="btn btn-sm btn-outline-secondary ms-2" :disabled="index === 0">↑</button>
          <button @click="moveOptionDown(index)" class="btn btn-sm btn-outline-secondary ms-2" :disabled="index === sortedOptions.length - 1">↓</button>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import {ROOT_URL} from "../../config.js";

export default {
  props: {
    question: Object,
    index: Number,
  },
  data() {
    return {
      answer: this.question.type === 'MULTIPLE_CHOICE' ? [] : null,
      sortedOptions: [...this.question.options],
    };
  },
  created() {
    console.log(this.question.imageUrl)
    if (this.question.type === 'RATING') {
      this.answer = this.sortedOptions.map(option => option.id);
      this.$emit('update:answer', this.answer);
    }
  },
  methods: {
    ROOT_URL() {
      return ROOT_URL
    },
    moveOptionUp(index) {
      if (index > 0) {
        const option = this.sortedOptions.splice(index, 1)[0];
        this.sortedOptions.splice(index - 1, 0, option);
        this.updateAnswer();
      }
    },
    moveOptionDown(index) {
      if (index < this.sortedOptions.length - 1) {
        const option = this.sortedOptions.splice(index, 1)[0];
        this.sortedOptions.splice(index + 1, 0, option);
        this.updateAnswer();
      }
    },
    updateAnswer() {
      this.answer = this.sortedOptions.map(option => option.id);
      this.$emit('update:answer', this.answer);
    },
  },
  watch: {
    sortedOptions: {
      handler(newSortedOptions) {
        this.updateAnswer();
      },
      deep: true,
    },
  },
};
</script>
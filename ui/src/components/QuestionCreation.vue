<template>
  <div class="position-relative mb-3">
    <div class="card" style="padding-right: 40px;">
      <div class="card-body">
        <input v-model="question.text" class="form-control mb-2" placeholder="Текст вопроса" />
        <div class="mb-2">
          <label for="questionType" class="form-label">Тип вопроса:</label>
          <select v-model="question.type" id="questionType" class="form-select">
            <option value="SINGLE_CHOICE">Один вариант</option>
            <option value="MULTIPLE_CHOICE">Несколько вариантов</option>
            <option value="NUMERIC">Числовой</option>
            <option value="TEXT">Текстовый</option>
            <option value="RATING">Рейтинг</option>
          </select>
        </div>
        <div v-if="question.type !== 'NUMERIC' && question.type !== 'TEXT'" class="mt-2">
          <div v-for="(option, index) in question.options" :key="index" class="mb-2 d-flex align-items-center">
            <input v-model="option.text" class="form-control" placeholder="Текст опции" />
            <button @click="removeOption(index)" class="btn btn-danger btn-sm ms-2">x</button>
          </div>
          <button @click="addOption" class="btn btn-success btn-sm">+ Добавить вариант</button>
        </div>
        <div class="mt-2">
          <label for="questionImage" class="form-label">Изображение вопроса:</label>
          <input type="file" @change="onFileChange" id="questionImage" class="form-control" />
          <button v-if="question.imageUrl" @click="removeImage" class="btn btn-danger btn-sm mt-2">Удалить изображение</button>
        </div>
        <div v-if="question.imageUrl" class="mt-2">
          <img :src="question.imageUrl" alt="Question Image" style="max-width: 100%;" />
        </div>
      </div>
    </div>
    <button @click="removeQuestion" class="btn btn-danger btn-sm position-absolute top-0 end-0 h-100" style="width: 30px; opacity: 0.7;">
      <span>-</span>
    </button>
  </div>
</template>

<script>
export default {
  props: {
    question: Object,
  },
  methods: {
    addOption() {
      this.question.options.push({
        id: null,
        questionId: null,
        text: '',
      });
    },
    removeOption(index) {
      this.question.options.splice(index, 1);
    },
    removeQuestion() {
      this.$emit('removeQuestion');
    },
    onFileChange(event) {
      const file = event.target.files[0];
      console.log('Выбран файл в компоненте вопроса:', file);
      if (file) {
        const questionIndex = this.$parent.questions.indexOf(this.question);
        this.$parent.images[questionIndex] = file;
        this.question.imageUrl = URL.createObjectURL(file);
      }
    },
    removeImage() {
      const questionIndex = this.$parent.questions.indexOf(this.question);
      this.$parent.images[questionIndex] = null;
      this.question.imageUrl = null;
      this.$refs.questionImage.value = '';
    }
  },
  watch: {
    question: {
      handler(newQuestion) {
        this.$emit('updateQuestion', newQuestion);
      },
      deep: true,
    },
  },
};
</script>
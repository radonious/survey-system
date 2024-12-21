<template>
    <div class="container mt-5">
        <h1 class="text-center mb-4">Конструктор опросов</h1>
        <div class="mb-3">
            <label for="formTitle" class="form-label">Название опроса:</label>
            <input v-model="title" id="formTitle" class="form-control" placeholder="Введите название опроса" />
        </div>
        <div class="mb-3">
            <label for="formDescription" class="form-label">Описание опроса:</label>
            <textarea v-model="description" id="formDescription" class="form-control"
                placeholder="Введите описание опроса"></textarea>
        </div>
        <div v-for="(question, index) in questions" :key="index" class="mb-3">
            <QuestionComponent :question="question" @updateQuestion="updateQuestion(index, $event)"
                @removeQuestion="removeQuestion(index)" @updateImage="updateImage(index, $event)" />
        </div>
        <div class="d-flex justify-content-center">
            <button @click="addQuestion" class="btn btn-light btn-lg mb-3 w-100" style="opacity: 0.7;">
                <span class="text-secondary">+</span>
            </button>
        </div>
        <div class="d-flex justify-content-center">
            <button @click="createform" class="btn btn-success">Создать опрос</button>
        </div>
    </div>
    <div class="d-flex justify-content-center mt-4">
        <button @click="goBack" class="btn btn-secondary">Назад</button>
    </div>
    <br>
</template>

<script>
import QuestionComponent from '../components/QuestionCreation.vue';
import { API_BASE_URL } from '../../config.js';
import { authService } from '@/service/authService';

export default {
    components: {
        QuestionComponent,
    },
    data() {
        return {
            questions: [],
            creatorId: authService.getUserId(),
            creatorName: authService.getUsername(),
            title: '',
            description: '',
            images: [],
        };
    },
    methods: {
        addQuestion() {
            this.questions.push({
                id: null,
                formId: null,
                text: '',
                type: 'SINGLE_CHOICE',
                imageUrl: null,
                options: [],
            });
            this.images.push(null);
        },
        updateQuestion(index, updatedQuestion) {
            this.questions[index] = updatedQuestion;
        },
        removeQuestion(index) {
            this.questions.splice(index, 1);
            this.images.splice(index, 1);
        },

        updateImage(questionIndex, file) {
            console.log('Index in updateImage: ' + questionIndex)
            console.log('File in updateImage: ' + file)
            if (questionIndex !== -1) {
                this.images[questionIndex] = file;
                console.log(`Файл сохранён для вопроса с ID ${questionIndex}:`, file);
            }
        },

        async createform() {
            const form = {
                id: null,
                creatorId: this.creatorId,
                creatorName: this.creatorName,
                title: this.title,
                description: this.description,
                questions: this.questions,
            };

            const formData = new FormData();
            formData.append(
                'formRequestDTO',
                new Blob([JSON.stringify(form)], { type: 'application/json' })
            );

            const imageIndexes = [];
            this.images.forEach((image, index) => {
                if (image) {
                    formData.append('images', image);
                    imageIndexes.push(index);
                }
            });

            formData.append('imageIndexes', JSON.stringify(imageIndexes));

            console.log('JSON:', formData.get('formRequestDTO'));
            console.log('Images:', formData.getAll('images'));
            console.log('Image Indexes:', formData.get('imageIndexes'));

            try {
                const response = await authService.fetchWithToken(`${API_BASE_URL}/forms`, {
                    method: 'POST',
                    body: formData,
                });

                if (!response.ok) {
                    const errorData = await response.json();
                    throw new Error(errorData.message);
                }

                const data = await response.json();
                console.log('Опрос успешно создан:', data);
                this.$router.push('/form-list');
            } catch (error) {
                console.error('Ошибка при создании опроса:', error);
            }
        },
        goBack() {
            this.$router.push('/form-list');
        },
    },
};
</script>
import { createRouter, createWebHistory } from "vue-router";
import WelcomePage from "../views/WelcomePage.vue";
import Login from "../views/Login.vue";
import Registration from "../views/Registration.vue";
import FormCreation from "@/views/FormCreation.vue";
import FormCompletion from "@/views/FormCompletion.vue";
import { authService } from "../service/authService";
import FormList from "@/views/FormList.vue";
import Statistics from "@/views/Statistics.vue";
import UserProfilePage from "@/views/UserProfilePage.vue";
import CompletedForm from "@/views/CompletedForm.vue";

const routes = [
  { path: "/", component: WelcomePage },
  { path: "/login", component: Login },
  { path: "/registration", component: Registration },
  { path: "/create-form", component: FormCreation },
  { path: "/profile", component: UserProfilePage },
  { path: "/complete-form/:formId", component: FormCompletion },
  { path: "/my-answers/:completionId", component: CompletedForm},
  { path: "/form-list", component: FormList },
  { path: "/statistics/:formId", component: Statistics },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

router.beforeEach((to, from, next) => {
  const publicRoutes = ["/", "/login", "/registration"];
  const protectedRoutes = [
    "/create-form",
    "/profile",
    "/complete-form/:formId",
    "/my-answers/:completionId",
    "/form-list",
    "/statistics/:formId",
  ];

  if (publicRoutes.includes(to.path) && authService.hasRefreshToken()) {
    next("/form-list");
  } else if (
    protectedRoutes.includes(to.path) &&
    !authService.hasRefreshToken()
  ) {
    next("/");
  } else {
    next();
  }
});

export default router;

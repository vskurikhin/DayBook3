import axios from "axios";
import { Link, useNavigate } from "react-router-dom";
import { useForm } from "react-hook-form";

import { useAuth } from "../../contexts/AuthContext";
import { STATUS } from "../../utils/utils";

import styles from "./Login.module.scss";

type FormValues = {
  username: string;
  password: string;
};

const Login = () => {
  const {
    handleSubmit,
    register,
    formState: { errors, touchedFields },
  } = useForm({
    defaultValues: {
      username: "",
      password: "",
    },
    mode: "onChange",
  });

  const navigate = useNavigate();

  const { login, setAuthenticationStatus } = useAuth();

  const onSubmit = async (values: FormValues) => {
    const user = {
      user_name: values.username,
      password: values.password,
    };

    try {
      setAuthenticationStatus(STATUS.PENDING);
      const response = await axios.post("/auth/api/v2/auth", user);
      setAuthenticationStatus(STATUS.SUCCEEDED);
      console.log("response: " + JSON.stringify(response))
      const { user: userObj, token, expires_at } = response.data.data;
      login(userObj, token, expires_at);
      navigate('/');
    } catch (error: unknown) {
      const err = error as any;
      alert(err?.response?.data?.error?.message ?? "Login failed");
    }
  };

  return (
    <div className={styles.container}>
      <div className={styles.formWrapper}>
        <form className={styles.form} onSubmit={handleSubmit(onSubmit)}>
          <h1 className={styles.formTitle}>Sign In</h1>
          <div className={styles.formGroup}>
            <input
              className={styles.input}
              type="text"
              id="username"
              aria-label="Username or Email"
              required
              placeholder="Username or Email"
              {...register("username", {
                required: { value: true, message: "This field is required." },
              })}
            />
            <div className={styles.validationError}>
              <span>
                {touchedFields.username && errors.username?.message}
              </span>
            </div>
          </div>
          <div className={styles.formGroup}>
            <input
              className={styles.input}
              type="password"
              id="password"
              required
              placeholder="Password"
              {...register("password", {
                required: { value: true, message: "Password is required." },
              })}
            />
            <div className={styles.validationError}>
              <span>
                {touchedFields.password && errors.password?.message}
              </span>
            </div>
          </div>
          <div className={styles.formGroup}>
            <button className={styles.submitButton} type="submit">
              Sign In
            </button>
          </div>
          <p className={styles.text}>
            <span>Don't have an account?</span>
            <Link className={styles.link} to="/sign-up">
              Sign Up
            </Link>
          </p>
        </form>
      </div>
    </div>
  );
};

export default Login;
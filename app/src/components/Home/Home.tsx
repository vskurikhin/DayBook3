import { useAuth } from "../../contexts/AuthContext";

import styles from "./Home.module.scss";

const Home = () => {
  const { user } = useAuth();

  return (
    <div className={styles.container}>
      <h1 className={styles.heading}>
      <span className={styles.colorTeal}>Welcome</span>
      <span className={styles.colorBlack}>{(user as { name: string }).name}</span></h1>
    </div>
  );
};

export default Home;
import { Link, useNavigate } from "react-router-dom";
import axios from "axios";

import { useAuth } from "../../contexts/AuthContext";

import styles from "./Navbar.module.scss";

const Navbar = () => {
  const { isAuthenticated, token, logout } = useAuth();
  const navigate = useNavigate();

  const logOutHandler = async () => {
    try {
      await axios.post(
        "/auth/api/v2/logout",
        {},
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );
      logout();
      navigate('/login');
    } catch (error) {
      console.log("Something went wrong.", error);
    }
  };

  return (
    <header className={styles.header}>
      <nav className={styles.navigation}>
        <div>
          <Link className={styles.brand} to="/">
            SVN
          </Link>
        </div>
        <div className={styles.navigationListContainer}>
          <ul className={styles.navigationList}>
            {!isAuthenticated && (
              <>
                <li className={styles.navigationItem}>
                  <Link className={styles.navigationLink} to="/login">
                    Login
                  </Link>
                </li>
              </>
            )}
            {isAuthenticated && (
              <>
                <li className={styles.navigationItem}>
                  <Link className={styles.navigationLink} to="/post">
                    Post
                  </Link>
                </li>
                <li className={styles.navigationItem}>
                  <button
                    className={styles.navigationLink}
                    onClick={logOutHandler}
                  >
                    Log out
                  </button>
                </li>
              </>
            )}
          </ul>
        </div>
      </nav>
    </header>
  );
};

export default Navbar;
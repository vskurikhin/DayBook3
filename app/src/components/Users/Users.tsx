import { useEffect, useRef, useState } from "react";
import axios from "axios";

import User from "./User/User";
import { useAuth } from "../../contexts/AuthContext";

import styles from './Users.module.scss';

type UserType = {
  id: string | number;
  name: string;
};

const Users = () => {
  const { token } = useAuth();
  const [users, setUsers] = useState<UserType[]>([]);
  const lastTokenRef = useRef<string | null>(null);

  useEffect(() => {
    if (!token) {
      return;
    }

    if (lastTokenRef.current === token) {
      return;
    }

    lastTokenRef.current = token;

    axios
      .get("/auth/api/v2/user/list", {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      })
      .then((res) => {
        setUsers(res.data.data);
      })
      .catch((error) => {
        console.log("Something went wrong.", error);
      });
  }, [token]);

  return (
    <div className={styles.container}>
      {users.map((user) => (
        <div key={user.id} className={styles.userContainer}>
            <User user={user}/>
        </div>
      ))}
    </div>
  );
};

export default Users;

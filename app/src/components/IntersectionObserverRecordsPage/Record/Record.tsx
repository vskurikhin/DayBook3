import styles from './Record.module.scss';
import { Link } from "react-router-dom";
import { marked } from "marked";
import { useAuth } from "../../../contexts/AuthContext";

const User = ({ record }: { record: any }) => {
    const date = new Date(record.refreshAt || record.postAt);
    const { isAuthenticated } = useAuth();

    const months = [
        "январь",
        "февраль",
        "март",
        "апрель",
        "май",
        "июнь",
        "июль",
        "август",
        "сентябрь",
        "октябрь",
        "ноябрь",
        "декабрь"
    ];

    const formatted =
        `${date.getFullYear()} ` +
        `${months[date.getMonth()]} ` +
        `${String(date.getDate()).padStart(2, "0")} ` +
        `${String(date.getHours()).padStart(2, "0")}:` +
        `${String(date.getMinutes()).padStart(2, "0")}`;
  return (
    <>
    <div className={styles.container}>
      <div className={styles.imageContainer}>
        <img
          className={styles.image}
          src="https://img.freepik.com/free-vector/businessman-character-avatar-isolated_24877-60111.jpg"
          alt="User Avatar"
        />
      </div>
      <div>
        <h2>{record.title}</h2>
        <p>
          { record.type === "Text" && (
            <div
              className={styles.outputStyle}
              dangerouslySetInnerHTML={{
                  __html: marked.parse(record.markdown || "")
              }}>
            </div>
          )}
          <div>
          { record.type !== "Text" && record.value || record.xml || JSON.stringify(record.json) }
          </div>
        </p>
      </div>
        <p className={styles.date}>
          {isAuthenticated && (
            <>
              <Link to={`/post/${record.id}/edit`} className={styles.editLink}><span
                className={styles.editIcon}>✏&nbsp;</span>
              </Link>
            </>
          ) || (
            <>
              <span
                className={styles.editIcon}>✏&nbsp;
              </span>
            </>
          )}
          <span>{formatted}</span>
        </p>
    </div>
    </>
  );
};

export default User;
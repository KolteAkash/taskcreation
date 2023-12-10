import styles from './LoginRegisterImg.module.css'
const LoginRegister = (k) => {
    const borderRadiusStyle = { 
        background: 'radial-gradient(circle, rgba(63,171,251,1) 0%, rgba(6,90,215,1) 100%)',
        width:k.width,
        borderBottomLeftRadius: k.side === 'left' ? "1rem" : "0",
        borderBottomRightRadius: k.side === 'right' ? "1rem" : "0",
        borderTopRightRadius: k.side === 'right' ? "1rem" : "0",
        borderTopLeftRadius: k.side === 'left' ? "1rem" : "0",
    };
    return (
        <>
            <div style={borderRadiusStyle} className={styles.mainDiv}>
                <div className={styles.backgroundBlur}>
                <img className={styles.backgroundImg} src={k.imgAddress} alt="sd" />
                </div>
                <div className={styles.allText}>
                <h1>{k.title}</h1>
                <p>{k.msg}</p>
                </div>
            </div>

        </>
    )
}
export default LoginRegister;
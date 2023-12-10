import styles from './button.module.css'
const ButtonName = (props)=>{
    const inputStyle = { 
        width:props.width,
        backgroundColor:props.color
    };
    return(
        <>
        <div className={styles.MainButton}>
        <button type={props.type} style={inputStyle}>{props.buttonName}</button>
        </div>
        </>
    )
}
export default ButtonName;
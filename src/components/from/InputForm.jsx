import styles from './inputStyle.module.css'
import TextField from '@mui/material/TextField';
const InputForm = (k) => {
    const inputStyle = { 
        width:k.width,
    };
    return (
        <>
            <div style={styles.MainInput}>
               
                    <TextField size="small" style={inputStyle}  value={k.value} name={k.name} type={k.type} onChange={k.onChange} label={k.placeholder} variant="outlined" autoComplete="off"/>
                {/* </Box> */}
                {/* <input type={k.type} style={inputStyle} className={styles.mainInput} value={k.value
                } name={k.name} onChange={k.onChange} placeholder={k.placeholder} autoComplete="off" /> */}
            </div>
        </>
    )
}
export default InputForm;
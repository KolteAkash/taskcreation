const Auth = () => {
    const userData = JSON.parse(sessionStorage.getItem("userData"));
    if (!userData || !userData.token) {
        return false;
    } else {
        return userData;
    }
}
export default Auth;
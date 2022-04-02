package cz.mendelu.xlinek.eduapp.utils;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class TokenInfo {
    private boolean isTokenValid = false;
    private String tokenMessage = "";
    private String name = "";
    private String surname = "";
    private String email = "";
    private String picture = "";
    private String role = "";
}



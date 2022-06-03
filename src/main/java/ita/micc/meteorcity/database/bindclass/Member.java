package ita.micc.meteorcity.database.bindclass;

import lombok.*;

@Getter
@Setter
@RequiredArgsConstructor
public class Member {

    private @NonNull String UUID;
    private @NonNull String role;
    private int IDCity;
}

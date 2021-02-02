import { Community } from "./community.model";

export class User {

    constructor(
        public id: string,
        public username: string,
        public avgScore: number,
        public community: Community,
    ) {}

    
}
package com.example.hoang.myapplication.Model;

public class Person {
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public Person(String name, String avatar) {
            this.name = name;
            this.avatar = avatar;
        }

        private String name;
        private String avatar;
    }